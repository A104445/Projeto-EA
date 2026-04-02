package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("PASSE_ANUAL")
@Getter @Setter @NoArgsConstructor
public class PasseAnualEntity extends TituloTransporteEntity {

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Override
    public boolean isValid() {
        LocalDate hoje = LocalDate.now();
        return dataFim != null && !hoje.isAfter(dataFim);
    }
}