package com.projeto_EA.AirBus.models.entities;

import com.projeto_EA.AirBus.models.DTOs.NotificacaoDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@DiscriminatorValue("PASSE_ANUAL")
@Getter @NoArgsConstructor
public class PasseAnualEntity extends TituloTransporteEntity {

    private static final int DIAS_AVISO = 30;

    @Setter @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Setter @Column(name = "data_fim")
    private LocalDate dataFim;

    @Override
    public String getTipo() { return "PASSE_ANUAL"; }

    @Override
    public boolean isValid() {
        LocalDate hoje = LocalDate.now();
        return dataFim != null && !hoje.isAfter(dataFim);
    }

    @Override
    public List<NotificacaoDTO> gerarNotificacoes() {
        if (dataFim == null) return List.of();
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataFim);
        if (diasRestantes < 0) {
            return List.of(new NotificacaoDTO("PASSE_EXPIRADO", "O teu passe anual expirou.", getId()));
        }
        if (diasRestantes <= DIAS_AVISO) {
            return List.of(new NotificacaoDTO("PASSE_EXPIRANDO",
                    "O teu passe anual expira em " + diasRestantes + " dia(s).", getId()));
        }
        return List.of();
    }
}