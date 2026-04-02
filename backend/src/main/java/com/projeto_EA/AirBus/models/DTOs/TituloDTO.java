package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor
public class TituloDTO {
    private Long ID;
    private String tipo;
    private String estado;
    private LocalDateTime dataAquisicao;
    private boolean valido;
    private Set<String> zonas;

    // PasseAnual
    private LocalDate dataInicio;
    private LocalDate dataFim;

    // BilheteIndividual
    private Boolean usado;
    private String carreiraNome;
    private String carreiraNum;

    // PacoteViagens
    private Integer numViagensTotais;
    private Integer saldoRestante;

    public TituloDTO(TituloTransporteEntity t) {
        this.ID = t.getID();
        this.tipo = t.getTipo();
        this.estado = t.getEstado();
        this.dataAquisicao = t.getDataAquisicao();
        this.valido = t.isValid();
        this.zonas = t.getZonas().stream()
                .map(ZonaGeograficaEntity::getNome)
                .collect(Collectors.toSet());

        if (t instanceof PasseAnualEntity p) {
            this.dataInicio = p.getDataInicio();
            this.dataFim = p.getDataFim();
        } else if (t instanceof BilheteIndividualEntity b) {
            this.usado = b.isUsado();
            this.dataFim = b.getDataFim();
            if (b.getCarreira() != null) {
                this.carreiraNome = b.getCarreira().getNome();
                this.carreiraNum = b.getCarreira().getNum();
            }
        } else if (t instanceof PacoteViagensEntity pv) {
            this.numViagensTotais = pv.getNumViagensTotais();
            this.saldoRestante = pv.getSaldoRestante();
        }
    }
}