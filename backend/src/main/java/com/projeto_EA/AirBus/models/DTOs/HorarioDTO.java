package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.HorarioEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor
public class HorarioDTO {
    private Long id;
    private Long carreiraId;
    private String carreiraNum;
    private String carreiraNome;
    private Long paragemId;
    private String paragemNome;
    private LocalTime horaChegada;
    private int diaSemana;

    public HorarioDTO(HorarioEntity h) {
        this.id = h.getId();
        this.carreiraId = h.getCarreira().getId();
        this.carreiraNum = h.getCarreira().getNum();
        this.carreiraNome = h.getCarreira().getNome();
        this.paragemId = h.getParagem().getId();
        this.paragemNome = h.getParagem().getNome();
        this.horaChegada = h.getHoraChegada();
        this.diaSemana = h.getDiaSemana();
    }
}
