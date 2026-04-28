package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.ParagemEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor
public class ParagemDTO {
    private Long id;
    private String nome;
    private String estado;
    private String qr;
    private Set<String> zonas;

    public ParagemDTO(ParagemEntity p) {
        this.id = p.getId();
        this.nome = p.getNome();
        this.estado = p.getEstado().name();
        this.qr = p.getQr();
        this.zonas = p.getZonas().stream()
                .map(z -> z.getNome())
                .collect(Collectors.toSet());
    }
}