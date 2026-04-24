package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.ParagemDTO;
import com.projeto_EA.AirBus.models.DTOs.VeiculoDTO;
import com.projeto_EA.AirBus.models.entities.CarreiraEntity;
import com.projeto_EA.AirBus.models.entities.HorarioEntity;
import com.projeto_EA.AirBus.models.entities.VeiculoEntity;
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

    @Transactional(readOnly = true)
    public List<CarreiraDTO> getCarreirasByParagemOrdenadoPorHorario(Long paragemID, LocalTime hora, int diaSemana) {
        List<CarreiraEntity> carreiras = carreiraRepository.findByParagemID(paragemID);
        if (carreiras.isEmpty()) return List.of();

        // procurar todos os horários da paragem neste dia (uma só query)
        List<HorarioEntity> horarios = horarioRepository.findByParagemAndDia(paragemID, diaSemana);

        // para cada carreira: encontrar a próxima chegada >= hora atual... se não houver, a última do dia.
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        Map<Long, LocalTime> proximaChegada = new HashMap<>();
        for (HorarioEntity h : horarios) {
            Long cid = h.getCarreira().getID();
            LocalTime t = h.getHoraChegada();
            // preferir a mais próxima futura (se já passaram todas, guardar a última...)
            if (!proximaChegada.containsKey(cid)) {
                proximaChegada.put(cid, t);
            } else {
                LocalTime atual = proximaChegada.get(cid);
                boolean atualPassou  = atual.isBefore(hora);
                boolean novaPassou   = t.isBefore(hora);
                if (atualPassou && !novaPassou) {
                    // nova é futura, atual já passou → preferir nova
                    proximaChegada.put(cid, t);
                } else if (atualPassou && novaPassou) {
                    // ambas passaram → guardar a mais recente (mais próxima de agora por baixo)
                    if (t.isAfter(atual)) proximaChegada.put(cid, t);
                } else if (!atualPassou && !novaPassou) {
                    // ambas são futuras → guardar a mais cedo
                    if (t.isBefore(atual)) proximaChegada.put(cid, t);
                }
            }
        }

        // ordenar: carreiras com horário futuro primeiro (por proximidade), depois as sem horário.
        return carreiras.stream()
                .sorted(Comparator
                        .<CarreiraEntity, Boolean>comparing(c -> proximaChegada.getOrDefault(c.getID(), null) == null)
                        .<LocalTime>thenComparing(c -> {
                            LocalTime t = proximaChegada.get(c.getID());
                            if (t == null) return LocalTime.MAX;
                            return t.isBefore(hora) ? t.plusHours(24) : t; // passado ficam no fim
                        }))
                .map(c -> {
                    CarreiraDTO dto = new CarreiraDTO(c);
                    LocalTime t = proximaChegada.get(c.getID());
                    if (t != null) dto.setProximaChegada(t.format(fmt));

                    // calcular o veículo alocado ao próximo horário
                    List<VeiculoEntity> veiculos = c.getVeiculos().stream()
                            .sorted(Comparator.comparing(VeiculoEntity::getID))
                            .collect(Collectors.toList());
                    if (!veiculos.isEmpty()) {
                        List<HorarioEntity> carreiraHorarios = horarios.stream()
                                .filter(h -> h.getCarreira().getID().equals(c.getID()))
                                .collect(Collectors.toList()); // já ordenados por horaChegada ASC
                        if (!carreiraHorarios.isEmpty()) {
                            int idx = carreiraHorarios.size() - 1;
                            for (int i = 0; i < carreiraHorarios.size(); i++) {
                                if (!carreiraHorarios.get(i).getHoraChegada().isBefore(hora)) {
                                    idx = i;
                                    break;
                                }
                            }
                            dto.setVeiculoProximo(new VeiculoDTO(veiculos.get(idx % veiculos.size())));
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}