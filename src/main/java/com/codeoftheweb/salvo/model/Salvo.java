package com.codeoftheweb.salvo.model;

import com.codeoftheweb.salvo.Util;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

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
    private List<String> salvoLocations = new ArrayList<>();

    //Relations
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayerID")
    private GamePlayer gpSalvo;

    public Salvo() {
    }

    public Salvo(GamePlayer gamePlayer, int turn, List<String> salvoLocations) {
        this.turn = turn;
        this.salvoLocations = salvoLocations;
        this.gpSalvo = gamePlayer;
    }

    //Getters
    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gpSalvo;
    }

    public Map<String, Object> makeSalvoDTO() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", this.getTurn());
        dto.put("player", this.getGamePlayer().getPlayer().getId());
        dto.put("locations", this.getSalvoLocations());
        return dto;
    }

    public Map<String, Object> makeHitsDTO(GamePlayer self, Map<String, Object> damages) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        GamePlayer opponent = Util.getOpponent(self);
        List<String> hitLocations = calculateHitLocations(self);
        dto.put("turn", this.getTurn());
        dto.put("hitLocations", hitLocations);
        dto.put("damages", calculateDamages(self, hitLocations, damages));
        dto.put("missed", calculateMissed(opponent, hitLocations));
        return dto;
    }

    private List<String> calculateHitLocations(GamePlayer self) {
        List<String> hits = new ArrayList<>();
        GamePlayer opponent = Util.getOpponent(self);

        for (Salvo salvo : opponent.getSalvoes()) {
            if (salvo.getTurn() == this.getTurn()) {
                for (Ship ship : self.getShip()) {
                    for (String location : ship.getShipLocations()) {
                        if (salvo.getSalvoLocations().contains(location)) {
                            hits.add(location);
                        }
                    }
                }
            }
        }
        return hits;
    }

    private Map<String, Object> calculateDamages(GamePlayer self, List<String> hitLocations, Map<String, Object> damages) {
        // Hits del turno
        int carrierHits = 0, battleshipHits = 0, submarineHits = 0, destroyerHits = 0, patrolboatHits = 0;
        GamePlayer opponent = Util.getOpponent(self);

        // Barcos del self
        for (Ship ship : self.getShips()) {
            for (String location : ship.getShipLocations()) {
                if (hitLocations.contains(location)) {
                    switch (ship.getType()) {
                        case "carrier" -> carrierHits++;
                        case "battleship" -> battleshipHits++;
                        case "submarine" -> submarineHits++;
                        case "destroyer" -> destroyerHits++;
                        case "patrolboat" -> patrolboatHits++;
                    }
                }
            }
        }

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("carrierHits", carrierHits);
        dto.put("battleshipHits", battleshipHits);
        dto.put("submarineHits", submarineHits);
        dto.put("destroyerHits", destroyerHits);
        dto.put("patrolboatHits", patrolboatHits);

        for (Map.Entry<String, Object> entry : damages.entrySet()) {
            switch (entry.getKey()) {
                case "carrier" -> {
                    // Actualiza el valor
                    damages.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()) + carrierHits);
                    // Genera DTO
                    dto.put(entry.getKey(), entry.getValue());
                }
                case "battleship" -> {
                    damages.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()) + battleshipHits);
                    dto.put(entry.getKey(), entry.getValue());
                }
                case "submarine" -> {
                    damages.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()) + submarineHits);
                    dto.put(entry.getKey(), entry.getValue());
                }
                case "destroyer" -> {
                    damages.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()) + destroyerHits);
                    dto.put(entry.getKey(), entry.getValue());
                }
                case "patrolboat" -> {
                    damages.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()) + patrolboatHits);
                    dto.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return dto;
    }

    private int calculateMissed(GamePlayer opponent, List<String> hitLocations) {
        int missed = 0;
        for (Salvo salvo : opponent.getSalvoes()) {
            if (this.getTurn() == salvo.getTurn()) {
                missed = salvo.getSalvoLocations().size() - hitLocations.size();
            }
        }
        return missed;
    }

    //Setters
    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public void setGpSalvo(GamePlayer gpSalvo) {
        this.gpSalvo = gpSalvo;
    }
}
