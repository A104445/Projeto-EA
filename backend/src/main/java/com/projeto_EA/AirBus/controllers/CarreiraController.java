package com.projeto_EA.AirBus.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.HorarioDTO;
import com.projeto_EA.AirBus.routes.Routes;
import com.projeto_EA.AirBus.services.CarreiraService;
import com.projeto_EA.AirBus.services.HorarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Routes.CARREIRAS)
@RequiredArgsConstructor
public class CarreiraController {

    private final CarreiraService carreiraService;
    private final HorarioService horarioService;

    @GetMapping
    public ResponseEntity<List<CarreiraDTO>> getAllCarreiras() {
        return ResponseEntity.ok(carreiraService.getAllCarreiras());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreiraDTO> getCarreiraByID(@PathVariable Long id) {
        return ResponseEntity.ok(carreiraService.getCarreiraByID(id));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<HorarioDTO>> getHorarios(
            @PathVariable Long id,
            @RequestParam(required = false) Integer day) {
        if (day != null) {
            return ResponseEntity.ok(horarioService.getByCarreiraAndDia(id, day));
        }
        return ResponseEntity.ok(horarioService.getByCarreira(id));
    }
}