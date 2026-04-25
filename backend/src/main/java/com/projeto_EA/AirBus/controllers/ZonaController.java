package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.ZonaDTO;
import com.projeto_EA.AirBus.repositories.ZonaGeograficaRepository;
import com.projeto_EA.AirBus.routes.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(Routes.ZONAS)
@RequiredArgsConstructor
public class ZonaController {

    private final ZonaGeograficaRepository zonaRepository;

    @GetMapping
    public ResponseEntity<List<ZonaDTO>> getAllZonas() {
        List<ZonaDTO> zonas = zonaRepository.findAll().stream()
                .map(ZonaDTO::new)
                .toList();
        return ResponseEntity.ok(zonas);
    }
}
