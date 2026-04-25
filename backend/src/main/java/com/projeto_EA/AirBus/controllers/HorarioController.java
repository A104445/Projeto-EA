package com.projeto_EA.AirBus.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto_EA.AirBus.models.DTOs.HorarioDTO;
import com.projeto_EA.AirBus.routes.Routes;
import com.projeto_EA.AirBus.services.HorarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(Routes.HORARIOS)
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService horarioService;

    @GetMapping
    public ResponseEntity<List<HorarioDTO>> getHorarios(
            @RequestParam(required = false) Long carreiraID,
            @RequestParam(required = false) Long paragemID) {

        if (carreiraID != null && paragemID != null) {
            return ResponseEntity.ok(horarioService.getByCarreiraAndParagem(carreiraID, paragemID));
        } else if (carreiraID != null) {
            return ResponseEntity.ok(horarioService.getByCarreira(carreiraID));
        } else if (paragemID != null) {
            return ResponseEntity.ok(horarioService.getByParagem(paragemID));
        }
        return ResponseEntity.badRequest().build();
    }
}