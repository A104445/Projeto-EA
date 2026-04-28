package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.ViagemEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class ViagemDTO {
    private Long id;
    private String estado;
    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;
    private boolean irregular;
    private String carreiraNum;
    private String carreiraNome;
    private String origemNome;
    private String destinoNome;
    private String tipoTitulo;
    private String matriculaVeiculo;

    public ViagemDTO(ViagemEntity v) {
        this.id = v.getId();
        this.estado = v.getEstado().name();
        this.dataHoraEntrada = v.getDataHoraEntrada();
        this.dataHoraSaida = v.getDataHoraSaida();
        this.irregular = v.isIrregular();
        this.carreiraNum = v.getCarreira().getNum();
        this.carreiraNome = v.getCarreira().getNome();
        this.origemNome = v.getOrigem().getNome();
        this.destinoNome = v.getDestino() != null ? v.getDestino().getNome() : null;
        this.tipoTitulo = v.getTitulo().getTipo();
        this.matriculaVeiculo = v.getVeiculo() != null ? v.getVeiculo().getMatricula() : null;
    }
}