package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import com.projeto_EA.AirBus.repositories.CarreiraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarreiraService {

    private final CarreiraRepository carreiraRepository;

    @Transactional(readOnly = true)
    public List<CarreiraDTO> getAllCarreiras() {
        return carreiraRepository.findByEstado(EstadoGeral.ATIVO).stream()
                .map(CarreiraDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CarreiraDTO getCarreiraByID(Long id) {
        return carreiraRepository.findById(id)
                .map(CarreiraDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("Carreira não encontrada!"));
    }
}