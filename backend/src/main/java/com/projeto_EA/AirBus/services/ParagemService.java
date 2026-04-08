package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.ParagemDTO;
import com.projeto_EA.AirBus.repositories.CarreiraRepository;
import com.projeto_EA.AirBus.repositories.ParagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParagemService {

    private final ParagemRepository paragemRepository;
    private final CarreiraRepository carreiraRepository;

    @Transactional(readOnly = true)
    public List<ParagemDTO> getAllParagens() {
        return paragemRepository.findByEstado("ATIVO").stream()
                .map(ParagemDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParagemDTO getByQr(String qr) {
        return paragemRepository.findByQr(qr)
                .filter(p -> "ATIVO".equals(p.getEstado()))
                .map(ParagemDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("QR code inválido ou paragem inativa!"));
    }

    @Transactional(readOnly = true)
    public List<CarreiraDTO> getCarreirasByParagem(Long paragemID) {
        return carreiraRepository.findByParagemID(paragemID).stream()
                .map(CarreiraDTO::new)
                .collect(Collectors.toList());
    }
}