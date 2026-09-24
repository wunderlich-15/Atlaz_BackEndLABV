package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.LoginRequestDTO;
import com.example.AtlazDB.dto.LoginResponseDTO;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);

        if (user == null || !user.getPasswordHash().equals(dto.getPassword())) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        }

        return ResponseEntity.ok(new LoginResponseDTO(user.getId(), user.getProfile().name(), user.getName()));
    }
}