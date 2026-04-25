package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.ParagemDTO;
import com.projeto_EA.AirBus.services.ParagemService;
import com.projeto_EA.AirBus.routes.Routes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(Routes.PARAGENS)
@RequiredArgsConstructor
public class ParagemController {

    private final ParagemService paragemService;

    @GetMapping
    public ResponseEntity<List<ParagemDTO>> getAllParagens() {
        return ResponseEntity.ok(paragemService.getAllParagens());
    }

    @GetMapping("/by-qr")
    public ResponseEntity<ParagemDTO> getByQr(@RequestParam String qr) {
        return ResponseEntity.ok(paragemService.getByQr(qr));
    }

    @GetMapping("/{id}/routes")
    public ResponseEntity<List<CarreiraDTO>> getCarreirasByParagem(
            @PathVariable Long id,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) Integer day) {
        if (time != null && day != null) {
            return ResponseEntity.ok(
                    paragemService.getCarreirasByParagemOrdenadoPorHorario(id, LocalTime.parse(time), day));
        }
        return ResponseEntity.ok(paragemService.getCarreirasByParagem(id));
    }
}