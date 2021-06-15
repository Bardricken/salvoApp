package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Salvo {
    //Properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ElementCollection
    @Column(name = "locations")
    private List<String> cells = new ArrayList<>();

    //Relations
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gpSalvo;

    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayer, int turn, List<String> cells) {
        this.turn = turn;
        this.cells = cells;
        this.gpSalvo = gamePlayer;
    }

    //Getters
    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getCells() {
        return cells;
    }

    public GamePlayer getGamePlayer() {
        return gpSalvo;
    }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getCells());
        return dto;
    }

    //Setters
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setCells(List<String> cells) {
        this.cells = cells;
    }

    public void setGpSalvo(GamePlayer gpSalvo) {
        this.gpSalvo = gpSalvo;
    }
}
