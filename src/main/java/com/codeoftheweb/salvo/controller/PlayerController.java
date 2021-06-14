package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Salvo;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.ShipServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private PlayerServiceImplement playerService;
    @Autowired
    private GamePlayerServiceImplement gamePlayerService;
    @Autowired
    private ShipServiceImplement shipService;
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

    @PostMapping("/games/players/{nn}/ships")
    public ResponseEntity<Object> addShip(@PathVariable long nn, @RequestBody Set<Ship> ships, Authentication authentication) {
        if (!Util.isGuest(authentication)) {
            GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(nn);
            Player player = playerService.findPlayerByEmail(authentication.getName());
            if (gamePlayer != null) {
                if (gamePlayer.getPlayer().getId() == player.getId()) {
                    if (gamePlayer.getShips().stream().count() == 0) {
                        for (Ship s : ships) {
                            gamePlayer.addShips(s);
                            shipService.saveShip(s);
                        }
                        return new ResponseEntity<>(Util.makeMap("OK", "Sus barcos han sido colocados exitosamente"), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(Util.makeMap("error", "Sus barcos ya se encuentran colocados"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(Util.makeMap("error", "El jugador no pertenece a este juego"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Util.makeMap("error", "No existen registros del jugador"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos necesarios"), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/games/players/{nn}/salvoes")
    public ResponseEntity<Object> addSalvoes(@PathVariable long nn, @RequestBody Salvo salvo, Authentication authentication) {
        if (!Util.isGuest(authentication)) {
            GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(nn);
            Player player = playerService.findPlayerByEmail(authentication.getName());
            if (gamePlayer != null) {
                if (gamePlayer.getPlayer().getId() == player.getId()) {
                    int hits = salvo.getLocations().size();
                    if (hits >= 1 && hits <= 5) {
                        int turn = salvo.getTurn();
//                        if (turn == 0 || ) {
//                            turn += 1;
//                        }else{
//                            turn
//                        }

//                        for (Ship s : ships) {
//                            gamePlayer.addShips(s);
//                            shipService.saveShip(s);
//                        }
                        return new ResponseEntity<>(Util.makeMap("OK", "Sus barcos han sido colocados exitosamente"), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(Util.makeMap("error", "Exediste el límite de disparos"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(Util.makeMap("error", "El jugador no pertenece a este juego"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Util.makeMap("error", "No existen registros del jugador"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos necesarios"), HttpStatus.UNAUTHORIZED);
        }
    }
}