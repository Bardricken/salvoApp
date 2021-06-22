package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.service.GamePlayerService;
import com.codeoftheweb.salvo.service.GameService;
import com.codeoftheweb.salvo.service.PlayerService;
import com.codeoftheweb.salvo.service.ScoreService;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.GameServiceImplement;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private GamePlayerService gamePlayerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GameService gameService;
    @Autowired
    ScoreService scoreService;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (!Util.isGuest(authentication)) {
            Player player = playerService.findPlayerByEmail(authentication.getName());
            dto.put("player", player.makePlayerDTO());
        } else {
            dto.put("player", "Guest");
        }
        dto.put("games", gameService.getGame().stream().map(Game::makeGameDTO).collect(toList()));
        return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Object> createGame(Authentication authentication) {
        if (!Util.isGuest(authentication)) {
            Player player = playerService.findPlayerByEmail(authentication.getName());
            Game game = gameService.saveGame(new Game(new Date()));
            GamePlayer gp = gamePlayerService.saveGamePlayer(new GamePlayer(game, player, new Date()));
            return new ResponseEntity<>(Util.makeMap("gpid", gp.getId()), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos"), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/game_view/{nn}")
    public ResponseEntity<Object> getGameView(@PathVariable Long nn, Authentication authentication) {
        GamePlayer self = gamePlayerService.findGamePlayerById(nn);
        Player player = playerService.findPlayerByEmail(authentication.getName());

        if (self.getPlayer().getId() == player.getId()) {
            return ResponseEntity.ok(makeGameViewDTO(self));
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos"), HttpStatus.UNAUTHORIZED);
        }
    }

    private Map<String, Object> makeGameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Map<String, Object> hits = new LinkedHashMap<>();
        Map<String, Object> damages = makeDamagesMap();
        Game game = gamePlayer.getGame();
        hits.put("self", gamePlayer.getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn))
                .map(s -> s.makeHitsDTO(gamePlayer, damages)).collect(toList()));
        resetDamages(damages);
        hits.put("opponent", Util.getOpponent(gamePlayer).getSalvoes().stream().sorted(Comparator.comparingInt(Salvo::getTurn))
                .map(s -> s.makeHitsDTO(Util.getOpponent(gamePlayer), damages)).collect(toList()));
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("created", gamePlayer.getGame().getCreationDate());
        dto.put("gameState", getState(gamePlayer));
        dto.put("gamePlayers", game.getGamePlayers().stream().map(GamePlayer::makeGamePlayerDTO).collect(toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(Ship::makeShipDTO).collect(toList()));
        dto.put("salvoes", game.getGamePlayers().stream().map(GamePlayer::makeSalvoDTO).flatMap(Collection::stream).collect(toList()));
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
}
