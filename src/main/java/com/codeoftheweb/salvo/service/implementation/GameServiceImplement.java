package com.codeoftheweb.salvo.service.implementation;

import com.codeoftheweb.salvo.model.Game;
import com.codeoftheweb.salvo.repository.GameRepository;
import com.codeoftheweb.salvo.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImplement implements GameService {
    @Autowired
    private GameRepository gameRepository;

    @Override
    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    @Override
    public List<Game> getGame() {
        return gameRepository.findAll();
    }

    @Override
    public Game updateGame(Game game) {
        return null;
    }

    @Override
    public boolean deleteGame(Long id) {
        return false;
    }

    @Override
    public Game findGameById(Long id) {
        return gameRepository.findById(id).get();
    }
}
