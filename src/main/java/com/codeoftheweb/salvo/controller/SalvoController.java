package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.model.GamePlayer;
import com.codeoftheweb.salvo.repository.GamePlayerRepository;
import com.codeoftheweb.salvo.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {
    @Autowired
    private GameRepository repoGame;
    @Autowired
    private GamePlayerRepository repoGamePLayer;

    @RequestMapping("/games")
    public List<Object> getGames() {
        return repoGame.findAll().stream().map(Game::makeGameDTO).collect(Collectors.toList());
    }

    @RequestMapping("/game_view/{nn}")
    public Map<String, Object> getGameView(@PathVariable Long nn) {
        Optional<GamePlayer> gp = repoGamePLayer.findById(nn);
        if (gp.isPresent()) {
            return gp.get().getGame().makeGameShipDTO(gp.get());
        } else {
            return null;
        }
    }

}
