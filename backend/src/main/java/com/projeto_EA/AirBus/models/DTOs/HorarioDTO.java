package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.HorarioEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor
public class HorarioDTO {
    private Long ID;
    private Long carreiraID;
    private String carreiraNum;
    private String carreiraNome;
    private Long paragemID;
    private String paragemNome;
    private LocalTime horaChegada;
    private int diaSemana;

    public HorarioDTO(HorarioEntity h) {
        this.ID = h.getID();
        this.carreiraID = h.getCarreira().getID();
        this.carreiraNum = h.getCarreira().getNum();
        this.carreiraNome = h.getCarreira().getNome();
        this.paragemID = h.getParagem().getID();
        this.paragemNome = h.getParagem().getNome();
        this.horaChegada = h.getHoraChegada();
        this.diaSemana = h.getDiaSemana();
    }
}