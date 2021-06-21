package com.codeoftheweb.salvo.model;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.service.implementation.ScoreServiceImplement;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Map<String, Object> hits = new LinkedHashMap<>();
        Map<String, Object> damages = makeDamagesMap();
        hits.put("self", gamePlayer.getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn))
                .map(s -> s.makeHitsDTO(gamePlayer, damages)).collect(toList()));
        resetDamages(damages);
        hits.put("opponent", Util.getOpponent(gamePlayer).getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn))
                .map(s -> s.makeHitsDTO(Util.getOpponent(gamePlayer), damages)).collect(toList()));
        dto.put("id", this.getId());
        dto.put("created", this.getCreationDate());
        dto.put("gameState", this.getState(gamePlayer));
        dto.put("gamePlayers", getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO).collect(toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(Ship::makeShipDTO).collect(toList()));
        dto.put("salvoes", getGamePlayers().stream().map(GamePlayer::makeSalvoDTO).flatMap(Collection::stream).collect(toList()));
        dto.put("hits", hits);
        return dto;
    }

    private Map<String, Object> makeDamagesMap() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("carrier", 0);
        dto.put("battleship", 0);
        dto.put("submarine", 0);
        dto.put("destroyer", 0);
        dto.put("patrolboat", 0);
        return dto;
    }

    private void resetDamages(Map<String, Object> damages) {
        damages.replaceAll((k, v) -> 0);
    }

    private String getState(GamePlayer self) {

        GamePlayer opponent = Util.getOpponent(self);

        if (self.getShips().isEmpty()) {
            return "PLACESHIPS";
        }

        if (opponent.getShips().isEmpty()) {
            return "WAITINGFOROPP";
        }

        if (self.getSalvoes().size() > opponent.getSalvoes().size()) {
            return "WAIT";
        }

        int selfTurn = self.getSalvoes().size();
        int opponentTurn = opponent.getSalvoes().size();

        if (self.getGame().getPlayers().size() == 2 && selfTurn == opponentTurn) {
            ScoreServiceImplement scoreService = new ScoreServiceImplement();
            Score score = new Score();
            int selfDamage = getDamage(self);
            int opponentDamage = getDamage(opponent);
            score.setGame(self.getGame());
            score.setPlayer(self.getPlayer());
            score.setFinishDate(new Date());
            if (selfDamage == 17 && opponentDamage == 17) {
                score.setScore(0.5);
                scoreService.saveScore(score);
                return "TIE";
            } else if (selfDamage == 17) {
                score.setScore(1);
                scoreService.saveScore(score);
                return "WON";
            } else if (opponentDamage == 17) {
                score.setScore(0);
                scoreService.saveScore(score);
                return "LOST";
            }
        }
        return "PLAY";
    }

    private int getDamage(GamePlayer gamePlayer) {
        int totalDamage = 0;
        GamePlayer opponent = Util.getOpponent(gamePlayer);

        for (Salvo salvo : gamePlayer.getSalvoes()) {
            for (String location : salvo.getSalvoLocations()) {
                for (Ship ship : opponent.getShips()) {
                    if (ship.getShipLocations().contains(location)) {
                        switch (ship.getType()) {
                            case "carrier", "battleship", "submarine", "destroyer", "patrolboat" -> totalDamage++;
                        }
                    }
                }
            }
        }
        return totalDamage;
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
