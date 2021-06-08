package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

@Entity
public class Player {
    //Properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String email;
    private String password;

    //Relations
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    Set<Score> scores;

    //Constructors
    public Player() {
    }

    public Player(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //Getters
    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
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
        dto.put("id", this.getId());
        dto.put("email", this.getEmail());
        return dto;
    }

    /*public List<Player> getGames() {
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(toList());
    }*/

    //Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //Add
    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

}
