package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.service.GamePlayerService;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository repoGame;
    @Autowired
    private GamePlayerRepository repoGamePLayer;
    @Autowired
    private GamePlayerServiceImplement gamePlayerService;
    @Autowired
    private PlayerServiceImplement playerServiceImplement;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/games")
    public Map<String, Object> getGames(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (isGuest(authentication)) {
            dto.put("player", "Guest");
        } else {
            Player player = playerServiceImplement.findPlayerByEmail(authentication.getName());
            dto.put("player", player.makePlayerDTO());
        }
        dto.put("games", repoGame.findAll().stream().map(Game::makeGameDTO).collect(toList()));
        return dto;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @GetMapping("/game_view/{nn}")
    public ResponseEntity<?> getGameView(@PathVariable Long nn) {
        try {
            GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(nn);
            return ResponseEntity.ok(gamePlayer.getGame().makeGameShipDTO(gamePlayer));
        } catch (Exception exception) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        //Optional<GamePlayer> gp = gamePlayerService.findGamePlayerById(nn);
        //if (gp.isPresent()) {
        //    return gp.get().getGame().makeGameShipDTO(gp.get());
        //} else {
        //    return null;
        //}
    }

    @PostMapping("/players")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        try {
            if (playerServiceImplement.findPlayerByEmail(username) == null) {
                Player player = new Player(username, passwordEncoder.encode(password));
                playerServiceImplement.savePlayer(player);
                return new ResponseEntity(HttpStatus.CREATED);
            } else {
                return new ResponseEntity("Email in use", HttpStatus.FORBIDDEN);
            }
        } catch (Exception exception) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
