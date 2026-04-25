package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.models.entities.UserEntity;
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
    public ResponseEntity<List<TituloDTO>> getMeusTitulos(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(tituloService.getMeusTitulos(user.getID()));
    }

    @GetMapping("/mine/active")
    public ResponseEntity<List<TituloDTO>> getMeusTitulosAtivos(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(tituloService.getMeusTitulosAtivos(user.getID()));
    }

    @PostMapping("/annual-pass")
    public ResponseEntity<TituloDTO> adquirirPasseAnual(@AuthenticationPrincipal UserEntity user,
                                                         @Valid @RequestBody AdquirirPasseAnualDTO dto) {
        return ResponseEntity.ok(tituloService.adquirirPasseAnual(user.getID(), dto));
    }

    @PostMapping("/single-ticket")
    public ResponseEntity<TituloDTO> adquirirBilhete(@AuthenticationPrincipal UserEntity user,
                                                      @Valid @RequestBody AdquirirBilheteDTO dto) {
        return ResponseEntity.ok(tituloService.adquirirBilheteIndividual(user.getID(), dto));
    }

    @PostMapping("/trip-pack")
    public ResponseEntity<TituloDTO> adquirirPacote(@AuthenticationPrincipal UserEntity user,
                                                     @Valid @RequestBody AdquirirPacoteDTO dto) {
        return ResponseEntity.ok(tituloService.adquirirPacote(user.getID(), dto));
    }
}