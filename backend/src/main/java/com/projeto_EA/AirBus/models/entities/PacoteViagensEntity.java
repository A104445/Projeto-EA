package com.projeto_EA.AirBus.models.entities;

import com.projeto_EA.AirBus.models.DTOs.NotificacaoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@DiscriminatorValue("PACOTE_VIAGENS")
@Getter @NoArgsConstructor
public class PacoteViagensEntity extends TituloTransporteEntity {

    private static final int SALDO_MINIMO = 2;

    @Setter @Column(name = "num_viagens_totais")
    private int numViagensTotais;

    @Setter @Column(name = "saldo_restante")
    private int saldoRestante;

    @Override
    public String getTipo() { return "PACOTE_VIAGENS"; }

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

    @Override
    public List<NotificacaoDTO> gerarNotificacoes() {
        if (saldoRestante > SALDO_MINIMO) return List.of();
        return List.of(new NotificacaoDTO("SALDO_BAIXO",
                "Já só tens apenas " + saldoRestante + " viagem(s) restante(s) no teu pacote de viagens.",
                getId()));
    }
}