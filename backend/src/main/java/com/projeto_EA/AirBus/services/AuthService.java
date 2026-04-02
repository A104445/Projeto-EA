package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.repositories.UserRepository;
import com.projeto_EA.AirBus.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já registado!");
        }
        UserEntity user = new UserEntity();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDataNascimento(request.getDataNascimento());
        user.setTelefone(request.getTelefone());
        userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return new AuthResponseDTO(token, new UserDTO(user));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
        String token = jwtUtil.generateToken(user);
        return new AuthResponseDTO(token, new UserDTO(user));
    }
}