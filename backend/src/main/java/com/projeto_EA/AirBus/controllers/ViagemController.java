package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.models.entities.UserEntity;
import com.projeto_EA.AirBus.services.ViagemService;
import jakarta.validation.Valid;
import com.projeto_EA.AirBus.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UserRoutes.VIAGENS)
@RequiredArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    @PostMapping("/check-in")
    public ResponseEntity<ViagemDTO> checkIn(@AuthenticationPrincipal UserEntity user,
                                              @Valid @RequestBody CheckInRequestDTO dto) {
        return ResponseEntity.ok(viagemService.checkIn(user.getID(), dto));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ViagemDTO> checkOut(@AuthenticationPrincipal UserEntity user,
                                               @PathVariable Long id,
                                               @Valid @RequestBody CheckOutRequestDTO dto) {
        return ResponseEntity.ok(viagemService.checkOut(user.getID(), id, dto));
    }

    @GetMapping("/active")
    public ResponseEntity<ViagemDTO> getViagemAtiva(@AuthenticationPrincipal UserEntity user) {
        return viagemService.getViagemAtiva(user.getID())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<ViagemDTO>> getHistorico(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(viagemService.getHistorico(user.getID()));
    }
}