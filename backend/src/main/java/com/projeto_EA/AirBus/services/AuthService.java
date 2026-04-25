package com.projeto_EA.AirBus.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.projeto_EA.AirBus.models.DTOs.AuthResponseDTO;
import com.projeto_EA.AirBus.models.DTOs.GoogleLoginRequestDTO;
import com.projeto_EA.AirBus.models.DTOs.LoginRequestDTO;
import com.projeto_EA.AirBus.models.DTOs.RegisterRequestDTO;
import com.projeto_EA.AirBus.models.DTOs.UserDTO;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.repositories.UserRepository;
import com.projeto_EA.AirBus.security.JwtUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    private final NetHttpTransport httpTransport = new NetHttpTransport();
    private final GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Value("${google.oauth.client-id:}")
    private String googleClientId;

    @Value("${google.oauth.client-secret:}")
    private String googleClientSecret;

    @Value("${google.oauth.redirect-uri:}")
    private String googleRedirectUri;

    private GoogleIdTokenVerifier googleVerifier;

    @PostConstruct
    void initGoogleVerifier() {
        if (googleClientId != null && !googleClientId.isBlank()) {
            googleVerifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
        }
    }

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

    public AuthResponseDTO loginWithGoogle(GoogleLoginRequestDTO request) {
        if (googleVerifier == null || googleClientSecret == null || googleClientSecret.isBlank()) {
            throw new IllegalStateException("Login Google não configurado no servidor.");
        }

        Payload payload;
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    httpTransport, jsonFactory,
                    googleClientId, googleClientSecret,
                    request.getCode(), googleRedirectUri
            ).execute();

            GoogleIdToken idToken = googleVerifier.verify(tokenResponse.getIdToken());
            if (idToken == null) {
                throw new IllegalArgumentException("Token Google inválido.");
            }
            payload = idToken.getPayload();
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao validar login com Google.", e);
        }

        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new IllegalArgumentException("Conta Google sem e-mail verificado.");
        }

        String email = payload.getEmail().trim().toLowerCase();
        String name = (String) payload.get("name");

        UserEntity user = userRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email, name));

        return new AuthResponseDTO(jwtUtil.generateToken(user), new UserDTO(user));
    }

    private UserEntity createGoogleUser(String email, String name) {
        UserEntity user = new UserEntity();
        user.setNome((name != null && !name.isBlank()) ? name.trim() : email.split("@")[0]);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userRepository.save(user);
    }
}
