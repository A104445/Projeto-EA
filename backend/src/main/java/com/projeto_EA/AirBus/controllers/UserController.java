package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.UserDTO;
import com.projeto_EA.AirBus.models.DTOs.UserUpdateDTO;
import com.projeto_EA.AirBus.security.UserDetailsAdapter;
import com.projeto_EA.AirBus.services.UserService;
import com.projeto_EA.AirBus.routes.Routes;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Routes.UTILIZADORES)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return ResponseEntity.ok(userService.getProfile(principal.getUser().getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(@AuthenticationPrincipal UserDetailsAdapter principal,
                                                  @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(principal.getUser().getId(), dto));
    }
}