package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.ParagemDTO;
import com.projeto_EA.AirBus.services.ParagemService;
import com.projeto_EA.AirBus.routes.UserRoutes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(UserRoutes.PARAGENS)
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
    public ResponseEntity<List<CarreiraDTO>> getCarreirasByParagem(@PathVariable Long id) {
        return ResponseEntity.ok(paragemService.getCarreirasByParagem(id));
    }
}