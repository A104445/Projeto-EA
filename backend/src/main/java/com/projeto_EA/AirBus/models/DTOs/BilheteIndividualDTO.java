package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.BilheteIndividualEntity;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BilheteIndividualDTO extends TituloDTO {

    private final boolean usado;
    private final LocalDate dataFim;
    private final String carreiraNome;
    private final String carreiraNum;

    BilheteIndividualDTO(BilheteIndividualEntity b) {
        super(b);
        this.usado = b.isUsado();
        this.dataFim = b.getDataFim();
        this.carreiraNome = b.getCarreira() != null ? b.getCarreira().getNome() : null;
        this.carreiraNum  = b.getCarreira() != null ? b.getCarreira().getNum()  : null;
    }
}
