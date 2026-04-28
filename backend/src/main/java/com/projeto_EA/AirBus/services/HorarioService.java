package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.HorarioDTO;
import com.projeto_EA.AirBus.repositories.HorarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioRepository horarioRepository;

    @Transactional(readOnly = true)
    public List<HorarioDTO> getByCarreira(Long carreiraID) {
        return horarioRepository.findByCarreiraId(carreiraID).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> getByParagem(Long paragemID) {
        return horarioRepository.findByParagemId(paragemID).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> getByCarreiraAndParagem(Long carreiraID, Long paragemID) {
        return horarioRepository.findByCarreiraIdAndParagemId(carreiraID, paragemID).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HorarioDTO> getByCarreiraAndDia(Long carreiraID, int diaSemana) {
        return horarioRepository.findByCarreiraIdAndDiaSemana(carreiraID, diaSemana).stream()
                .map(HorarioDTO::new)
                .collect(Collectors.toList());
    }
}