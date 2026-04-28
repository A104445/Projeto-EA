package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.PacoteViagensEntity;
import lombok.Getter;

@Getter
public class PacoteViagensDTO extends TituloDTO {

    private final int numViagensTotais;
    private final int saldoRestante;

    PacoteViagensDTO(PacoteViagensEntity pv) {
        super(pv);
        this.numViagensTotais = pv.getNumViagensTotais();
        this.saldoRestante    = pv.getSaldoRestante();
    }
}
