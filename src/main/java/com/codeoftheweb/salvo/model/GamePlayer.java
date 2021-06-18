package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class GamePlayer {
    //Properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date joinDate;

    //Relations
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gameID")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "playerID")
    private Player player;

    @OneToMany(mappedBy = "gpShip", fetch = FetchType.EAGER)
    Set<Ship> ship = new LinkedHashSet<>();

    @OneToMany(mappedBy = "gpSalvo", fetch = FetchType.EAGER)
    Set<Salvo> salvo = new LinkedHashSet<>();

    //Constructors
    public GamePlayer() {
    }

    public GamePlayer(Game game, Player player, Date joinDate) {
        this.game = game;
        this.player = player;
        this.joinDate = joinDate;
    }

    //Getters
    public long getId() {
        return id;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    public Set<Ship> getShip() {
        return ship;
    }

    public Map<String, Object> makeGamePlayerDTO() {
        Score score = getPlayer().getScore(getGame());
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("joinDate", this.getJoinDate());
        dto.put("player", this.getPlayer().makePlayerDTO());
        if (score != null) {
            dto.put("score", score.getScore());
        } else {
            dto.put("score", 0);
        }
        return dto;
    }

    public List<Ship> getShips() {
        return new ArrayList<>(this.ship);
    }

    public List<Object> makeSalvoDTO() {
        return salvo.stream().map(Salvo::makeSalvoDTO).collect(toList());
    }

    public LinkedHashSet<Salvo> getSalvoes() {
        return new LinkedHashSet<>(this.salvo);
    }

    //Setters
    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addShips(Ship nShip) {
        nShip.setGamePlayer(this);
        this.ship.add(nShip);
    }

    public void addSalvoes(Salvo nSalvo) {
        nSalvo.setGpSalvo(this);
        this.salvo.add(nSalvo);
    }
}
