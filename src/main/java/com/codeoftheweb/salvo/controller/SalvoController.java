package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.Util;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.model.Salvo;
import com.codeoftheweb.salvo.service.implementation.GamePlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import com.codeoftheweb.salvo.service.implementation.SalvoServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private PlayerServiceImplement playerService;
    @Autowired
    private GamePlayerServiceImplement gamePlayerService;
    @Autowired
    private SalvoServiceImplement salvoService;

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
                            return new ResponseEntity<>(Util.makeMap("OK", "El salvo de registro correctamente"), HttpStatus.CREATED);
                        } else {
                            return new ResponseEntity<>(Util.makeMap("error", "Espera a que sea tu turno"), HttpStatus.FORBIDDEN);
                        }
                    } else {
                        return new ResponseEntity<>(Util.makeMap("error", "Debes lanzar entre 1 y 5 disparos"), HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(Util.makeMap("error", "Espera a tu oponente"), HttpStatus.UNAUTHORIZED);
                }
            } else {
                return new ResponseEntity<>(Util.makeMap("error", "El jugador no pertenece a este juego"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>(Util.makeMap("error", "No posee permisos necesarios"), HttpStatus.UNAUTHORIZED);
        }
    }
}
