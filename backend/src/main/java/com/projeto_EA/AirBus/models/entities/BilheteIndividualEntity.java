package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("BILHETE_INDIVIDUAL")
@Getter @Setter @NoArgsConstructor
public class BilheteIndividualEntity extends TituloTransporteEntity {

    @Column(name = "usado")
    private boolean usado = false;

    @Column(name = "data_fim_bilhete")
    private LocalDate dataFim;

    @ManyToOne
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    @Override
    public boolean isValid() {
        return !usado && (dataFim == null || !LocalDate.now().isAfter(dataFim));
    }

    public void marcarUsado() {
        this.usado = true;
    }
}