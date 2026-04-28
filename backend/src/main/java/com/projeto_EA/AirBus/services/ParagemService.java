package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.ParagemDTO;
import com.projeto_EA.AirBus.models.DTOs.VeiculoDTO;
import com.projeto_EA.AirBus.models.entities.CarreiraEntity;
import com.projeto_EA.AirBus.models.entities.HorarioEntity;
import com.projeto_EA.AirBus.models.entities.VeiculoEntity;
import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import com.projeto_EA.AirBus.repositories.CarreiraRepository;
import com.projeto_EA.AirBus.repositories.HorarioRepository;
import com.projeto_EA.AirBus.repositories.ParagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParagemService {

    private final ParagemRepository paragemRepository;
    private final CarreiraRepository carreiraRepository;
    private final HorarioRepository horarioRepository;

    @Transactional(readOnly = true)
    public List<ParagemDTO> getAllParagens() {
        return paragemRepository.findByEstado(EstadoGeral.ATIVO).stream()
                .map(ParagemDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ParagemDTO getByQr(String qr) {
        return paragemRepository.findByQr(qr)
                .filter(p -> EstadoGeral.ATIVO == p.getEstado())
                .map(ParagemDTO::new)
                .orElseThrow(() -> new IllegalArgumentException("QR code inválido ou paragem inativa!"));
    }

    @Transactional(readOnly = true)
    public List<CarreiraDTO> getCarreirasByParagem(Long paragemID) {
        return carreiraRepository.findByParagemId(paragemID, EstadoGeral.ATIVO).stream()
                .map(CarreiraDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarreiraDTO> getCarreirasByParagemOrdenadoPorHorario(Long paragemID, LocalTime hora, int diaSemana) {
        List<CarreiraEntity> carreiras = carreiraRepository.findByParagemId(paragemID, EstadoGeral.ATIVO);
        if (carreiras.isEmpty()) return List.of();

        List<HorarioEntity> horarios = horarioRepository.findByParagemAndDia(paragemID, diaSemana);
        Map<Long, LocalTime> proximaChegada = calcularProximaChegada(horarios, hora);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        return carreiras.stream()
                .sorted(comparadorPorChegada(proximaChegada, hora))
                .map(c -> toCarreiraDTOComContexto(c, horarios, proximaChegada, hora, fmt))
                .collect(Collectors.toList());
    }

    private Map<Long, LocalTime> calcularProximaChegada(List<HorarioEntity> horarios, LocalTime hora) {
        Map<Long, LocalTime> proximaChegada = new HashMap<>();
        for (HorarioEntity h : horarios) {
            Long cid = h.getCarreira().getId();
            proximaChegada.merge(cid, h.getHoraChegada(), (atual, nova) -> melhorHorario(atual, nova, hora));
        }
        return proximaChegada;
    }

    private LocalTime melhorHorario(LocalTime atual, LocalTime nova, LocalTime hora) {
        boolean atualPassou = atual.isBefore(hora);
        boolean novaPassou  = nova.isBefore(hora);
        if (atualPassou && !novaPassou) return nova;
        if (atualPassou && novaPassou)  return nova.isAfter(atual) ? nova : atual;
        return nova.isBefore(atual) ? nova : atual;
    }

    private Comparator<CarreiraEntity> comparadorPorChegada(Map<Long, LocalTime> proximaChegada, LocalTime hora) {
        return Comparator
                .<CarreiraEntity, Boolean>comparing(c -> !proximaChegada.containsKey(c.getId()))
                .<LocalTime>thenComparing(c -> {
                    LocalTime t = proximaChegada.get(c.getId());
                    if (t == null) return LocalTime.MAX;
                    return t.isBefore(hora) ? t.plusHours(24) : t;
                });
    }

    private CarreiraDTO toCarreiraDTOComContexto(CarreiraEntity c, List<HorarioEntity> todosHorarios,
                                                  Map<Long, LocalTime> proximaChegada, LocalTime hora,
                                                  DateTimeFormatter fmt) {
        CarreiraDTO dto = new CarreiraDTO(c);
        LocalTime t = proximaChegada.get(c.getId());
        if (t != null) dto.setProximaChegada(t.format(fmt));

        List<VeiculoEntity> veiculos = c.getVeiculos().stream()
                .sorted(Comparator.comparing(VeiculoEntity::getId))
                .collect(Collectors.toList());
        if (veiculos.isEmpty()) return dto;

        List<HorarioEntity> carreiraHorarios = todosHorarios.stream()
                .filter(h -> h.getCarreira().getId().equals(c.getId()))
                .collect(Collectors.toList());
        if (carreiraHorarios.isEmpty()) return dto;

        int idx = carreiraHorarios.size() - 1;
        for (int i = 0; i < carreiraHorarios.size(); i++) {
            if (!carreiraHorarios.get(i).getHoraChegada().isBefore(hora)) {
                idx = i;
                break;
            }
        }
        dto.setVeiculoProximo(new VeiculoDTO(veiculos.get(idx % veiculos.size())));
        return dto;
    }
}