package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(PlayerRepository repoPlayer, GameRepository repoGame, GamePlayerRepository repoGamePlayer, ShipRepository repoShip, SalvoRepository repoSalvo, ScoreRepository repoScore) {
        return (args) -> {

            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder.encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov", passwordEncoder.encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder.encode("kb"));
            Player player4 = new Player("t.almeida@ctu.gov", passwordEncoder.encode("mole"));

            //Players
            repoPlayer.save(player1);
            repoPlayer.save(player2);
            repoPlayer.save(player3);
            repoPlayer.save(player4);

            Game game1 = new Game(new Date());
            Game game2 = new Game(Date.from(new Date().toInstant().plusSeconds(3600)));
            Game game3 = new Game(Date.from(new Date().toInstant().plusSeconds(7200)));
            Game game4 = new Game(Date.from(new Date().toInstant().plusSeconds(10800)));
            Game game5 = new Game(Date.from(new Date().toInstant().plusSeconds(14400)));
            Game game6 = new Game(Date.from(new Date().toInstant().plusSeconds(18000)));
            Game game7 = new Game(Date.from(new Date().toInstant().plusSeconds(21600)));
            Game game8 = new Game(Date.from(new Date().toInstant().plusSeconds(25200)));

            //Games
            repoGame.save(game1);
            repoGame.save(game2);
            repoGame.save(game3);
            repoGame.save(game4);
            repoGame.save(game5);
            repoGame.save(game6);
            repoGame.save(game7);
            repoGame.save(game8);

            GamePlayer gmp1 = new GamePlayer(game1, player1, new Date());
            GamePlayer gmp2 = new GamePlayer(game1, player2, new Date());
            GamePlayer gmp3 = new GamePlayer(game2, player1, new Date());
            GamePlayer gmp4 = new GamePlayer(game2, player2, new Date());
            GamePlayer gmp5 = new GamePlayer(game3, player2, new Date());
            GamePlayer gmp6 = new GamePlayer(game3, player4, new Date());
            GamePlayer gmp7 = new GamePlayer(game4, player2, new Date());
            GamePlayer gmp8 = new GamePlayer(game4, player1, new Date());
            GamePlayer gmp9 = new GamePlayer(game5, player4, new Date());
            GamePlayer gmp10 = new GamePlayer(game5, player1, new Date());
            GamePlayer gmp11 = new GamePlayer(game6, player3, new Date());
            GamePlayer gmp13 = new GamePlayer(game7, player4, new Date());
            GamePlayer gmp15 = new GamePlayer(game8, player3, new Date());
            GamePlayer gmp16 = new GamePlayer(game8, player4, new Date());

            //GamePlayers
            //Game 1
            repoGamePlayer.save(gmp1);
            repoGamePlayer.save(gmp2);
            //Game 2
            repoGamePlayer.save(gmp3);
            repoGamePlayer.save(gmp4);
            //Game 3
            repoGamePlayer.save(gmp5);
            repoGamePlayer.save(gmp6);
            //Game 4
            repoGamePlayer.save(gmp7);
            repoGamePlayer.save(gmp8);
            //Game 5
            repoGamePlayer.save(gmp9);
            repoGamePlayer.save(gmp10);
            //Game 6
            repoGamePlayer.save(gmp11);
            //Game 7
            repoGamePlayer.save(gmp13);
            //Game 8
            repoGamePlayer.save(gmp15);
            repoGamePlayer.save(gmp16);

            //Ship Locations
            //Game 1
            repoShip.save(new Ship(gmp1, "Destroyer", Arrays.asList("H2", "H3", "H4")));
            repoShip.save(new Ship(gmp1, "Submarine", Arrays.asList("E1", "F1", "G1")));
            repoShip.save(new Ship(gmp1, "Patrol Boat", Arrays.asList("B4", "B5")));
            repoShip.save(new Ship(gmp2, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp2, "Patrol Boat", Arrays.asList("B4", "B5")));
            //Game 2
            repoShip.save(new Ship(gmp3, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp3, "Patrol Boat", Arrays.asList("C6", "C7")));
            repoShip.save(new Ship(gmp4, "Submarine", Arrays.asList("A2", "A3", "A4")));
            repoShip.save(new Ship(gmp4, "Patrol Boat", Arrays.asList("G6", "H6")));
            //Game 3
            repoShip.save(new Ship(gmp5, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp5, "Patrol Boat", Arrays.asList("C6", "C7")));
            repoShip.save(new Ship(gmp6, "Submarine", Arrays.asList("A2", "A3", "A4")));
            repoShip.save(new Ship(gmp6, "Patrol Boat", Arrays.asList("G6", "H6")));
            //Game 4
            repoShip.save(new Ship(gmp7, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp7, "Patrol Boat", Arrays.asList("C6", "C7")));
            repoShip.save(new Ship(gmp8, "Submarine", Arrays.asList("A2", "A3", "A4")));
            repoShip.save(new Ship(gmp8, "Patrol Boat", Arrays.asList("G6", "H6")));
            //Game 5
            repoShip.save(new Ship(gmp9, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp9, "Patrol Boat", Arrays.asList("C6", "C7")));
            repoShip.save(new Ship(gmp10, "Submarine", Arrays.asList("A2", "A3", "A4")));
            repoShip.save(new Ship(gmp10, "Patrol Boat", Arrays.asList("G6", "H6")));
            //Game 6
            repoShip.save(new Ship(gmp11, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp11, "Patrol Boat", Arrays.asList("C6", "C7")));
            //Game 7 (Empty)
            //Game 8
            repoShip.save(new Ship(gmp15, "Destroyer", Arrays.asList("B5", "C5", "D5")));
            repoShip.save(new Ship(gmp15, "Patrol Boat", Arrays.asList("C6", "C7")));
            repoShip.save(new Ship(gmp16, "Submarine", Arrays.asList("A2", "A3", "A4")));
            repoShip.save(new Ship(gmp16, "Patrol Boat", Arrays.asList("G6", "H6")));

            //Salvoes
            repoSalvo.save(new Salvo(gmp1, 1, Arrays.asList("B5", "C5", "F1")));
            repoSalvo.save(new Salvo(gmp2, 1, Arrays.asList("B4", "B5", "B6")));
            repoSalvo.save(new Salvo(gmp1, 2, Arrays.asList("F2", "D5")));
            repoSalvo.save(new Salvo(gmp2, 2, Arrays.asList("E1", "H3", "A2")));
            repoSalvo.save(new Salvo(gmp3, 1, Arrays.asList("A2", "A4", "G6")));
            repoSalvo.save(new Salvo(gmp4, 1, Arrays.asList("B5", "D5", "C7")));
            repoSalvo.save(new Salvo(gmp3, 2, Arrays.asList("A3", "H6")));
            repoSalvo.save(new Salvo(gmp4, 2, Arrays.asList("C5", "C6")));
            repoSalvo.save(new Salvo(gmp5, 1, Arrays.asList("G6", "H6", "A4")));
            repoSalvo.save(new Salvo(gmp6, 1, Arrays.asList("H1", "H2", "H3")));
            repoSalvo.save(new Salvo(gmp5, 2, Arrays.asList("A2", "A3", "D8")));
            repoSalvo.save(new Salvo(gmp6, 2, Arrays.asList("E1", "F2", "G3")));
            repoSalvo.save(new Salvo(gmp7, 1, Arrays.asList("A3", "A4", "F7")));
            repoSalvo.save(new Salvo(gmp8, 1, Arrays.asList("B5", "C6", "H1")));
            repoSalvo.save(new Salvo(gmp7, 2, Arrays.asList("A2", "G6", "H6")));
            repoSalvo.save(new Salvo(gmp8, 2, Arrays.asList("C5", "C7", "D5")));
            repoSalvo.save(new Salvo(gmp9, 1, Arrays.asList("A1", "A2", "A3")));
            repoSalvo.save(new Salvo(gmp10, 1, Arrays.asList("B5", "B6", "C7")));
            repoSalvo.save(new Salvo(gmp9, 2, Arrays.asList("G6", "G7", "G8")));
            repoSalvo.save(new Salvo(gmp10, 2, Arrays.asList("C6", "D6", "E6")));
            repoSalvo.save(new Salvo(null, 3, Arrays.asList("")));
            repoSalvo.save(new Salvo(gmp10, 3, Arrays.asList("H1", "H8")));

            //Scores
            repoScore.save(new Score(1.0, new Date(), game1, player1));
            repoScore.save(new Score(0.5, new Date(), game1, player2));
            repoScore.save(new Score(0.0, new Date(), game2, player1));
            repoScore.save(new Score(0.5, new Date(), game2, player2));
            repoScore.save(new Score(1.0, new Date(), game2, player2));
            repoScore.save(new Score(0.5, new Date(), game2, player4));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {
    @Autowired
    PlayerServiceImplement servPlayer;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputMail -> {
            Player player = servPlayer.findPlayerByEmail(inputMail);
            if (player != null) {
                return new User(player.getEmail(), player.getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputMail);
            }
        });
    }
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    PlayerServiceImplement servPlayer;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/games", "/api/players", "/api/login").permitAll()
                .antMatchers("/web/game.html", "/api/game_view/**").hasAuthority("USER")
                .antMatchers("/web/**").permitAll()
                .antMatchers("/h2-console/**").permitAll().anyRequest().authenticated()
                .and().csrf().ignoringAntMatchers("/h2-console/**")
                .and().headers().frameOptions().sameOrigin();

        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}