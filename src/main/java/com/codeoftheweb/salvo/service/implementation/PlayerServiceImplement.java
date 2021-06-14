package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.repository.PlayerRepository;
import com.codeoftheweb.salvo.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImplement implements PlayerService {
    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public List<Player> getPlayer() {
        return playerRepository.findAll();
    }

    @Override
    public Player updatePlayer(Player gamePlayer) {
        return null;
    }

    @Override
    public boolean deletePlayer(Long id) {
        return false;
    }

    @Override
    public Player findPlayerById(Long id) {
        return playerRepository.findById(id).get();
    }

    @Override
    public Player findPlayerByEmail(String mail) {
        Player player = playerRepository.findPlayerByEmail(mail);
        if (player != null) {
            return player;
        }
        return null;
    }
}
