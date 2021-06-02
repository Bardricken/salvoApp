package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {
    //Properties
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String type;

    @ElementCollection
    @Column(name = "locations")
    private List<String> locations = new ArrayList<>();

    //Relations
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gpShip;

    //Constructors
    public Ship() {
    }

    public Ship(GamePlayer gamePlayer, String type, List<String> locations) {
        this.gpShip = gamePlayer;
        this.type = type;
        this.locations = locations;
    }

    //Getters
    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public List<String> getLocations() {
        return locations;
    }

    public Map<String, Object> makeShipDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("type", this.getType());
        dto.put("locations", this.getLocations());
        return dto;
    }

    //Setters
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gpShip = gamePlayer;
    }

    public void setLocations(String location) {
        this.locations.add(location);
    }
}
