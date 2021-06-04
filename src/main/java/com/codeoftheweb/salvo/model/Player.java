package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Player {
    //Properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;

    //Relations
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    //Constructors
    public Player() {
    }

    public Player(String userName) {
        this.userName = userName;
    }

    //Getters
    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public Score getScore(Game game) {
        Optional<Score> score = getScores().stream().filter(s -> s.getGame().equals(game)).findFirst();
        if (score.isPresent()) {
            return score.get();
        }
        return null;
    }

    public Map<String, Object> makePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("playerId", this.getId());
        dto.put("email", this.getUserName());
        return dto;
    }

    /*public List<Player> getGames() {
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(toList());
    }*/

    //Setters
    public void setUserName(String userName) {
        this.userName = userName;
    }

    //Add
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

}
