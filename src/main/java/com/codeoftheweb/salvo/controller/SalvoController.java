package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Salvo;
import com.codeoftheweb.salvo.service.GamePlayerService;
import com.codeoftheweb.salvo.service.PlayerService;
import com.codeoftheweb.salvo.service.SalvoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private PlayerService playerService;
    @Autowired
    private GamePlayerService gamePlayerService;
    @Autowired
    private SalvoService salvoService;

    @PostMapping("/games/players/{nn}/salvoes")
    public ResponseEntity<Object> addSalvoes(@PathVariable long nn, @RequestBody Salvo nSalvo, Authentication authentication) {
        if (!Util.isGuest(authentication)) {
            GamePlayer self = gamePlayerService.findGamePlayerById(nn);
            Player player = playerService.findPlayerByEmail(authentication.getName());
            GamePlayer opponent = Util.getOpponent(self);

            if (self.getPlayer().getId() == player.getId()) {
                if (!opponent.getShip().isEmpty()) {
                    int hits = nSalvo.getSalvoLocations().size();
                    if (hits >= 1 && hits <= 5) {
                        int playerTurn = self.getSalvoes().stream().mapToInt(Salvo::getTurn).max().orElse(0);
                        int opponentTurn = opponent.getSalvoes().stream().mapToInt(Salvo::getTurn).max().orElse(0);
                        if (playerTurn <= opponentTurn) {
                            int turn;
                            if (playerTurn == 0) {
                                turn = 1;
                            } else {
                                turn = playerTurn + 1;
                            }
                            nSalvo.setTurn(turn);
                            self.addSalvoes(nSalvo);
                            salvoService.saveSalvo(nSalvo);
                            return new ResponseEntity<>(Util.makeMap("OK", "Successfully registered"), HttpStatus.CREATED);
                        } else {
                            return new ResponseEntity<>(Util.makeMap("error", "Wait your turn"), HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>(Util.makeMap("error", "You must fire between 1 and 5 shots"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(Util.makeMap("error", "Wait for your opponent"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Util.makeMap("error", "The player doesn't belong to this game"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "You don't have permissions"), HttpStatus.UNAUTHORIZED);
        }
    }
}
