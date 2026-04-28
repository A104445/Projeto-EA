package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@DiscriminatorValue("BILHETE_INDIVIDUAL")
@Getter @NoArgsConstructor
public class BilheteIndividualEntity extends TituloTransporteEntity {

    @Column(name = "usado")
    private boolean usado = false;

    @Setter @Column(name = "data_fim_bilhete")
    private LocalDate dataFim;

    @Setter @ManyToOne
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    @Override
    public String getTipo() { return "BILHETE_INDIVIDUAL"; }

    @Override
    public boolean isValid() {
        return !usado && (dataFim == null || !LocalDate.now().isAfter(dataFim));
    }

    @Override
    public void validarParaCheckIn(CarreiraEntity carreira, ParagemEntity paragem) {
        if (!this.carreira.getId().equals(carreira.getId())) {
            throw new IllegalStateException("Bilhete não é válido para esta carreira!");
        }
        // zona já está coberta pela carreira — não é necessário verificar
    }

    @Override
    public boolean isIrregular(Set<ZonaGeograficaEntity> zonasPercorridas) {
        return false; // bilhete individual cobre a carreira inteira
    }

    @Override
    public void aoFinalizarViagem() {
        marcarUsado();
    }

    public void marcarUsado() {
        this.usado = true;
    }
}