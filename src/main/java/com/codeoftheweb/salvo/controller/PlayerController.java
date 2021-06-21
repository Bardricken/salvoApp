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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private GamePlayerServiceImplement gamePlayerService;
    @Autowired
    private PlayerServiceImplement playerService;
    @Autowired
    private GameServiceImplement gameService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/players")
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {
        if (playerService.findPlayerByEmail(email) == null) {
            Player player = new Player(email, passwordEncoder.encode(password));
            playerService.savePlayer(player);
            return new ResponseEntity<>(Util.makeMap("OK", "Ha sido registrado exitosamente"), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "Email en uso"), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/game/{nn}/players")
    public ResponseEntity<Object> joinGame(@PathVariable Long nn, Authentication authentication) {
        boolean exist;
        long counter;

        if (!Util.isGuest(authentication)) {
            Game game = gameService.findGameById(nn);
            if (game != null) {
                Player player = playerService.findPlayerByEmail(authentication.getName());
                counter = game.getPlayers().size();
                exist = game.getPlayers().contains(player);

                if (counter < 2 && !exist) {
                    GamePlayer gp = gamePlayerService.saveGamePlayer(new GamePlayer(game, player, new Date()));
                    return new ResponseEntity<>(Util.makeMap("gpid", gp.getId()), HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("El juego está lleno", HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>("No existe tal juego", HttpStatus.FORBIDDEN);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos"), HttpStatus.UNAUTHORIZED);
        }
    }
}
