package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.HorarioDTO;
import com.projeto_EA.AirBus.repositories.HorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepository;

    public List<HorarioDTO> getByCarreira(Long carreiraID) {
        return horarioRepository.findByCarreiraID(carreiraID).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }

    public List<HorarioDTO> getByParagem(Long paragemID) {
        return horarioRepository.findByParagemID(paragemID).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }

    public List<HorarioDTO> getByCarreiraAndParagem(Long carreiraID, Long paragemID) {
        return horarioRepository.findByCarreiraIDAndParagemID(carreiraID, paragemID).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }

    public List<HorarioDTO> getByCarreiraAndDia(Long carreiraID, int diaSemana) {
        return horarioRepository.findByCarreiraIDAndDiaSemana(carreiraID, diaSemana).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }
}