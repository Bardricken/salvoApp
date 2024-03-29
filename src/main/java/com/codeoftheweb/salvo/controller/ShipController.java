package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Ship;
import com.codeoftheweb.salvo.service.GamePlayerService;
import com.codeoftheweb.salvo.service.PlayerService;
import com.codeoftheweb.salvo.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class ShipController {
    @Autowired
    private GamePlayerService gamePlayerService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ShipService shipService;

    @PostMapping("/games/players/{nn}/ships")
    public ResponseEntity<Object> addShip(@PathVariable long nn, @RequestBody Set<Ship> ships, Authentication authentication) {
        if (!Util.isGuest(authentication)) {
            GamePlayer gamePlayer = gamePlayerService.findGamePlayerById(nn);
            Player player = playerService.findPlayerByEmail(authentication.getName());
            if (gamePlayer != null) {
                if (gamePlayer.getPlayer().getId() == player.getId()) {
                    if (gamePlayer.getShips().size() == 0) {
                        for (Ship s : ships) {
                            gamePlayer.addShips(s);
                            shipService.saveShip(s);
                        }
                        return new ResponseEntity<>(Util.makeMap("OK", "Successfully registered"), HttpStatus.CREATED);
                    } else {
                        return new ResponseEntity<>(Util.makeMap("error", "Your ships are already in place"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(Util.makeMap("error", "The player doesn't belong to this game"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Util.makeMap("error", "There are no player records"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "You don't have permissions"), HttpStatus.UNAUTHORIZED);
        }
    }
}
