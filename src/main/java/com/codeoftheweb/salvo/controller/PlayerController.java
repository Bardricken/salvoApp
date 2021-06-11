package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.model.Player;
import com.codeoftheweb.salvo.service.implementation.PlayerServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PlayerController {
    @Autowired
    private PlayerServiceImplement playerServiceImplement;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/players")
    public ResponseEntity<Object> register(@RequestParam String email, @RequestParam String password) {
        if (playerServiceImplement.findPlayerByEmail(email) == null) {
            Player player = new Player(email, passwordEncoder.encode(password));
            playerServiceImplement.savePlayer(player);
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity("Email in use", HttpStatus.FORBIDDEN);
        }
    }
}
