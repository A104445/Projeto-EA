package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("PACOTE_VIAGENS")
@Getter @Setter @NoArgsConstructor
public class PacoteViagensEntity extends TituloTransporteEntity {

    @Column(name = "num_viagens_totais")
    private int numViagensTotais;

    @Column(name = "saldo_restante")
    private int saldoRestante;

    @Override
    public boolean isValid() {
        return saldoRestante > 0;
    }

    @Override
    public void aoFinalizarViagem() {
        descontarViagem();
    }

    public void descontarViagem() {
        if (saldoRestante > 0) saldoRestante--;
    }
}