package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.CarreiraEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor
public class CarreiraDTO {
    private Long ID;
    private String num;
    private String nome;
    private String estado;
    private List<ParagemDTO> paragens;
    private Integer ocupacaoAtual;
    private Integer capacidade;

    public CarreiraDTO(CarreiraEntity c) {
        this.ID = c.getID();
        this.num = c.getNum();
        this.nome = c.getNome();
        this.estado = c.getEstado();
        this.paragens = c.getParagens().stream()
                .map(ParagemDTO::new)
                .collect(Collectors.toList());
        if (c.getVeiculo() != null) {
            this.ocupacaoAtual = c.getVeiculo().getOcupacaoAtual();
            this.capacidade = c.getVeiculo().getCapacidade();
        }
    }
}