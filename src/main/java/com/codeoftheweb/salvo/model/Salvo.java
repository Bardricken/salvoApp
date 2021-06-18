package com.codeoftheweb.salvo.model;

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

    public Map<String, Object> makeHitsDTO(GamePlayer gamePlayer, Map<String, Object> damages) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Optional<GamePlayer> opponent = gamePlayer.getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer() != gamePlayer.getPlayer()).findFirst();
        List<String> hitLocations = calculateHitLocations(opponent.get());
        dto.put("turn", this.getTurn());
        dto.put("hitLocations", hitLocations);
        dto.put("damages", calculateDamages(opponent.get(), hitLocations, damages));
        dto.put("missed", calculateMissed(hitLocations));
        return dto;
    }

    private List<String> calculateHitLocations(GamePlayer gamePlayer) {
        List<String> hits = new ArrayList<>();
        // Celdas de los salvos
        for (String cell : this.getCells()) {
            // Barcos del oponente
            for (Ship ship : gamePlayer.getShips()) {
                // Ubicaciones del barco
                for (String location : ship.getShipLocations()) {
                    if (cell.equals(location)) {
                        hits.add(location);
                    }
                }
            }
        }
        return hits;
    }

    private Map<String, Object> calculateDamages(GamePlayer opponent, List<String> hitLocations, Map<String, Object> damages) {
        // Hits del turno
        int carrierHits = 0, battleshipHits = 0, submarineHits = 0, destroyerHits = 0, patrolboatHits = 0;

        for (Ship ship : opponent.getShips()) {
            for (String location : ship.getShipLocations()) {
                for (String hit : hitLocations) {
                    if (location.equals(hit)) {
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

    private int calculateMissed(List<String> hitLocations) {
        return 5 - hitLocations.size();
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
