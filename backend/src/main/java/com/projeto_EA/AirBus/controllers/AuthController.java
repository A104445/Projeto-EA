package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.AuthResponseDTO;
import com.projeto_EA.AirBus.models.DTOs.LoginRequestDTO;
import com.projeto_EA.AirBus.models.DTOs.RegisterRequestDTO;
import com.projeto_EA.AirBus.services.AuthService;
import jakarta.validation.Valid;
import com.projeto_EA.AirBus.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserRoutes.AUTH)
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
}