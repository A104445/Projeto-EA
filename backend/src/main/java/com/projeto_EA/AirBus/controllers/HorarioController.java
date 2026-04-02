package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.HorarioDTO;
import com.projeto_EA.AirBus.services.HorarioService;
import com.projeto_EA.AirBus.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UserRoutes.HORARIOS)
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