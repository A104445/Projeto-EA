package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.ZonaGeograficaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZonaDTO {
    private Long ID;
    private String nome;

    public ZonaDTO(ZonaGeograficaEntity z) {
        this.ID = z.getID();
        this.nome = z.getNome();
    }
}
