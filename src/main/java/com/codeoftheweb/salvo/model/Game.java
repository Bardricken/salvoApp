package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity
public class Game {
    //Properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private Date creationDate;

    //Relations
    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    Set<Score> scores;

    //Constructors
    public Game() {
    }

    public Game(Date creationDate) {
        this.creationDate = creationDate;
    }

    //Getters
    public long getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public List<GamePlayer> getGamePlayers() {
        return new ArrayList<>(this.gamePlayers);
    }

    public List<Score> getScores() {
        return new ArrayList<>(this.scores);
    }

    public Map<String, Object> makeGameDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO).collect(toList()));
        dto.put("scores", getScores().stream().map(Score::makeScoreDTO).collect(toList()));
        return dto;
    }

    public Map<String, Object> makeGameViewDTO(GamePlayer self) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Map<String, Object> hits = new LinkedHashMap<>();
        Optional<GamePlayer> opponent = self.getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer() != self.getPlayer()).findFirst();
        hits.put("self", self.getSalvoes().stream().map(s -> s.makeHitsDTO(self)).collect(toList()));
        if (opponent.isPresent()) {
            hits.put("opponent", opponent.get().getSalvoes().stream().map(s -> s.makeHitsDTO(opponent.get())).collect(toList()));
        } else {
            hits.put("opponent", new ArrayList<>());
        }
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gameState", "PLACESHIPS");
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO).collect(toList()));
        dto.put("ships", self.getShips().stream().map(Ship::makeShipDTO).collect(toList()));
        dto.put("salvoes", getGamePlayers().stream().map(GamePlayer::makeSalvoDTO).flatMap(Collection::stream).collect(toList()));
        dto.put("hits", hits);
        return dto;
    }

    public List<Player> getPlayers() {
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(toList());
    }

    //Setters
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    //Add
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }
}
