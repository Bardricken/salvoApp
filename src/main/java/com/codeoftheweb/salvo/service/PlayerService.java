package com.codeoftheweb.salvo.service;

import com.codeoftheweb.salvo.model.Player;

import java.util.List;

public interface PlayerService {
    Player savePlayer(Player player);

    List<Player> getPlayer();

    Player updatePlayer(Player gamePlayer);

    boolean deletePlayer(Long id);

    Player findPlayerById(Long id);

    Player findPlayerByEmail(String mail);
}
