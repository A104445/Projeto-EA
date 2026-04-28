package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.security.UserDetailsAdapter;
import com.projeto_EA.AirBus.services.TituloService;
import jakarta.validation.Valid;
import com.projeto_EA.AirBus.routes.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Routes.TITULOS)
@RequiredArgsConstructor
public class TituloController {

    private final TituloService tituloService;

    @GetMapping("/mine")
    public ResponseEntity<List<TituloDTO>> getMeusTitulos(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return ResponseEntity.ok(tituloService.getMeusTitulos(principal.getUser().getId()));
    }

    @GetMapping("/mine/active")
    public ResponseEntity<List<TituloDTO>> getMeusTitulosAtivos(@AuthenticationPrincipal UserDetailsAdapter principal) {
        return ResponseEntity.ok(tituloService.getMeusTitulosAtivos(principal.getUser().getId()));
    }

    @PostMapping("/annual-pass")
    public ResponseEntity<TituloDTO> adquirirPasseAnual(@AuthenticationPrincipal UserDetailsAdapter principal,
                                                         @Valid @RequestBody AdquirirPasseAnualDTO dto) {
        return ResponseEntity.ok(tituloService.adquirirPasseAnual(principal.getUser().getId(), dto));
    }

    @PostMapping("/single-ticket")
    public ResponseEntity<TituloDTO> adquirirBilhete(@AuthenticationPrincipal UserDetailsAdapter principal,
                                                      @Valid @RequestBody AdquirirBilheteDTO dto) {
        return ResponseEntity.ok(tituloService.adquirirBilheteIndividual(principal.getUser().getId(), dto));
    }

    @PostMapping("/trip-pack")
    public ResponseEntity<TituloDTO> adquirirPacote(@AuthenticationPrincipal UserDetailsAdapter principal,
                                                     @Valid @RequestBody AdquirirPacoteDTO dto) {
        return ResponseEntity.ok(tituloService.adquirirPacote(principal.getUser().getId(), dto));
    }
}