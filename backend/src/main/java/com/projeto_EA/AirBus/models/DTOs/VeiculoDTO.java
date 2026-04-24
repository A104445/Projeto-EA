package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.VeiculoEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class VeiculoDTO {
    private Long ID;
    private String matricula;
    private int capacidade;
    private int ocupacaoAtual;

    public VeiculoDTO(VeiculoEntity v) {
        this.ID = v.getID();
        this.matricula = v.getMatricula();
        this.capacidade = v.getCapacidade();
        this.ocupacaoAtual = v.getOcupacaoAtual();
    }
}