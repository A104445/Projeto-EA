package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.PasseAnualEntity;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PasseAnualDTO extends TituloDTO {

    private final LocalDate dataInicio;
    private final LocalDate dataFim;

    PasseAnualDTO(PasseAnualEntity p) {
        super(p);
        this.dataInicio = p.getDataInicio();
        this.dataFim = p.getDataFim();
    }
}
