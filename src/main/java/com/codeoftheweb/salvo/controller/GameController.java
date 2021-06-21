package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
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
    private GamePlayerServiceImplement gamePlayerService;
    @Autowired
    private PlayerServiceImplement playerService;
    @Autowired
    private GameServiceImplement gameService;

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
            return ResponseEntity.ok(self.getGame().makeGameViewDTO(self));
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos"), HttpStatus.UNAUTHORIZED);
        }
    }


}
