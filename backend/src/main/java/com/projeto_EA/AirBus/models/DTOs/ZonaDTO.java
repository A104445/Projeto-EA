package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.ZonaGeograficaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZonaDTO {
    private Long id;
    private String nome;

    public ZonaDTO(ZonaGeograficaEntity z) {
        this.id = z.getId();
        this.nome = z.getNome();
    }
}
