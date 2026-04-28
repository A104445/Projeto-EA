package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.CarreiraEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor
public class CarreiraDTO {
    private Long id;
    private String num;
    private String nome;
    private String estado;
    private List<ParagemDTO> paragens;
    private String proximaChegada;
    private VeiculoDTO veiculoProximo;

    public CarreiraDTO(CarreiraEntity c) {
        this.id = c.getId();
        this.num = c.getNum();
        this.nome = c.getNome();
        this.estado = c.getEstado().name();
        this.paragens = c.getParagens().stream()
                .map(ParagemDTO::new)
                .collect(Collectors.toList());
    }
}