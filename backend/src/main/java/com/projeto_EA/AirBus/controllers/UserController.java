package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.UserDTO;
import com.projeto_EA.AirBus.models.DTOs.UserUpdateDTO;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.services.UserService;
import com.projeto_EA.AirBus.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserRoutes.UTILIZADORES)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getProfile(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(userService.getProfile(user.getID()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(@AuthenticationPrincipal UserEntity user,
                                                  @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(user.getID(), dto));
    }
}