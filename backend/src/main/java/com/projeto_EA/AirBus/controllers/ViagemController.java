package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.security.UserDetailsAdapter;
import com.projeto_EA.AirBus.services.ViagemService;
import jakarta.validation.Valid;
import com.projeto_EA.AirBus.routes.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Routes.VIAGENS)
@RequiredArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    @PostMapping("/check-in")
    public ResponseEntity<ViagemDTO> checkIn(@AuthenticationPrincipal UserDetailsAdapter principal,
                                              @Valid @RequestBody CheckInRequestDTO dto) {
        return ResponseEntity.ok(viagemService.checkIn(principal.getUser().getId(), dto));
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<ViagemDTO> checkOut(@AuthenticationPrincipal UserDetailsAdapter principal,
                                               @PathVariable Long id,
                                               @Valid @RequestBody CheckOutRequestDTO dto) {
        return ResponseEntity.ok(viagemService.checkOut(principal.getUser().getId(), id, dto));
    }

    @GetMapping("/active")
    public ResponseEntity<ViagemDTO> getViagemAtiva(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return viagemService.getViagemAtiva(principal.getUser().getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<ViagemDTO>> getHistorico(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return ResponseEntity.ok(viagemService.getHistorico(principal.getUser().getId()));
    }
}