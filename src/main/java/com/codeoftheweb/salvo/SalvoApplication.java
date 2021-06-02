package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(PlayerRepository repoPlayer, GameRepository repoGame, GamePlayerRepository repoGamePlayer, ShipRepository repoShip, SalvoRepository repoSalvo) {
        return (args) -> {
            Player player1 = new Player("juaas@gmail.com");
            Player player2 = new Player("karen@gmail.com");
            Player player3 = new Player("erika@gmail.com");
            Player player4 = new Player("emilia@gmail.com");
            Player player5 = new Player("luis@gmail.com");
            Player player6 = new Player("david@gmail.com");
            repoPlayer.save(player1);
            repoPlayer.save(player2);
            repoPlayer.save(player3);
            repoPlayer.save(player4);
            repoPlayer.save(player5);
            repoPlayer.save(player6);

            Game game1 = new Game(new Date());
            Game game2 = new Game(Date.from(new Date().toInstant().plusSeconds(3600)));
            Game game3 = new Game(Date.from(new Date().toInstant().plusSeconds(7200)));
            repoGame.save(game1);
            repoGame.save(game2);
            repoGame.save(game3);

            GamePlayer gmp1 = new GamePlayer(game1, player1, new Date());
            GamePlayer gmp2 = new GamePlayer(game1, player2, new Date());
            GamePlayer gmp3 = new GamePlayer(game2, player4, new Date());
            GamePlayer gmp4 = new GamePlayer(game2, player3, new Date());
            GamePlayer gmp5 = new GamePlayer(game3, player5, new Date());
            GamePlayer gmp6 = new GamePlayer(game3, player6, new Date());
            repoGamePlayer.save(gmp1);
            repoGamePlayer.save(gmp2);
            repoGamePlayer.save(gmp3);
            repoGamePlayer.save(gmp4);
            repoGamePlayer.save(gmp5);
            repoGamePlayer.save(gmp6);

            repoShip.save(new Ship(gmp1, "Destructor", Arrays.asList("H7", "I2", "J1", "A9")));
            repoShip.save(new Ship(gmp2, "Patrullero", Arrays.asList("F10", "F3", "B1", "J2")));
            repoShip.save(new Ship(gmp3, "Destructor", Arrays.asList("H7", "I2", "C1", "D2")));
            repoShip.save(new Ship(gmp4, "Patrullero", Arrays.asList("F4", "J2", "B1", "C7")));
            repoShip.save(new Ship(gmp5, "Destructor", Arrays.asList("H7", "I2", "D1", "F2")));
            repoShip.save(new Ship(gmp6, "Patrullero", Arrays.asList("G6", "J2", "B1", "G5")));

            repoSalvo.save(new Salvo(gmp1, 1, Arrays.asList("H7", "A2")));
            repoSalvo.save(new Salvo(gmp1, 2, Arrays.asList("G1", "A9")));
            repoSalvo.save(new Salvo(gmp2, 1, Arrays.asList("F10", "J2")));
            repoSalvo.save(new Salvo(gmp2, 2, Arrays.asList("B1", "A2")));
            repoSalvo.save(new Salvo(gmp3, 1, Arrays.asList("H7", "I2")));
            repoSalvo.save(new Salvo(gmp4, 2, Arrays.asList("C1", "D2")));
        };
    }
}
