package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.GameServiceImplement;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private GamePlayerServiceImplement gamePlayerService;
    @Autowired
    private PlayerServiceImplement playerService;
    @Autowired
    private GameServiceImplement gameService;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerService.findPlayerByEmail(authentication.getName());
            dto.put("player", player.makePlayerDTO());
        }
        dto.put("games", gameService.getGame().stream().map(Game::makeGameDTO).collect(toList()));
        return dto;
    }

    @PostMapping("/games")
    public ResponseEntity<Object> createGame(Authentication authentication) {
        if (isGuest(authentication)) {
            return new ResponseEntity<>(makeMap("error", "No posee permisos"), HttpStatus.UNAUTHORIZED);
        } else {
            Player player = playerService.findPlayerByEmail(authentication.getName());
            Game game = gameService.saveGame(new Game(new Date()));
            GamePlayer gp = gamePlayerService.saveGamePlayer(new GamePlayer(game, player, new Date()));
            return new ResponseEntity<>(makeMap("gpid", gp.getId()), HttpStatus.CREATED);
        }
    }

    @GetMapping("/game_view/{nn}")
    public ResponseEntity<Object> getGameView(@PathVariable Long nn, Authentication authentication) {

        GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(nn);
        Player player = playerService.findPlayerByEmail(authentication.getName());

        if (gamePlayer.getPlayer().getId() == player.getId()) {
            return ResponseEntity.ok(gamePlayer.getGame().makeGameShipDTO(gamePlayer));
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
