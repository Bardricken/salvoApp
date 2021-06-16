package com.codeoftheweb.salvo.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<String, Object> makeHitsDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Optional<GamePlayer> opponent = gamePlayer.getGame().getGamePlayers().stream().filter(gp -> gp.getPlayer() != gamePlayer.getPlayer()).findFirst();
        List<String> hitLocations = calculateHitLocations(opponent.get());
        dto.put("turn", this.getTurn());
        dto.put("hitLocations", hitLocations);
        dto.put("damages", calculateDamages(gamePlayer, opponent.get(), hitLocations));
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

    private Map<String, Object> calculateDamages(GamePlayer gamePlayer, GamePlayer opponent, List<String> hitLocations) {
        // Hits del turno
        int carrierHits = 0, battleshipHits = 0, submarineHits = 0, destroyerHits = 0, patrolboatHits = 0;
        // Hits totales
        int carrier = 0, battleship = 0, submarine = 0, destroyer = 0, patrolboat = 0;

        for (Ship ship : gamePlayer.getShips()) {
//            System.out.println("SHIP TYPE" + ship.getType());
//            System.out.println("HIT LOCATIONS" + hitLocations);
//            System.out.println("SHIP LOCATIONS" + ship.getShipLocations());
            List<String> hits = ship.getShipLocations();
            hits.retainAll(hitLocations);
//            System.out.println("HITS" + hits);
            switch (ship.getType()) {
                case "carrier":
                    carrierHits = hits.size();
                    break;
                case "battleship":
                    battleshipHits = hits.size();
                    break;
                case "submarine":
                    submarineHits = hits.size();
                    break;
                case "destroyer":
                    destroyerHits = hits.size();
                    break;
                case "patrolboat":
                    patrolboatHits = hits.size();
                    break;
            }
        }

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("carrierHits", carrierHits);
        dto.put("battleshipHits", battleshipHits);
        dto.put("submarineHits", submarineHits);
        dto.put("destroyerHits", destroyerHits);
        dto.put("patrolboatHits", patrolboatHits);
        dto.put("carrier", carrier);
        dto.put("battleship", battleship);
        dto.put("submarine", submarine);
        dto.put("destroyer", destroyer);
        dto.put("patrolboat", patrolboat);

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
