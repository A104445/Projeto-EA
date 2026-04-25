package com.projeto_EA.AirBus.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto_EA.AirBus.models.DTOs.AuthResponseDTO;
import com.projeto_EA.AirBus.models.DTOs.GoogleLoginRequestDTO;
import com.projeto_EA.AirBus.models.DTOs.LoginRequestDTO;
import com.projeto_EA.AirBus.models.DTOs.RegisterRequestDTO;
import com.projeto_EA.AirBus.routes.Routes;
import com.projeto_EA.AirBus.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Routes.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDTO> googleLogin(@Valid @RequestBody GoogleLoginRequestDTO request) {
        return ResponseEntity.ok(authService.loginWithGoogle(request));
    }
}
