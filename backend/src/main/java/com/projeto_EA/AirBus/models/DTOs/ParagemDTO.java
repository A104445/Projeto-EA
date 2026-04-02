package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.ParagemEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter @NoArgsConstructor
public class ParagemDTO {
    private Long ID;
    private String nome;
    private String estado;
    private double latitude;
    private double longitude;
    private Set<String> zonas;
    private Double distanciaKm;

    public ParagemDTO(ParagemEntity p) {
        this.ID = p.getID();
        this.nome = p.getNome();
        this.estado = p.getEstado();
        this.latitude = p.getLatitude();
        this.longitude = p.getLongitude();
        this.zonas = p.getZonas().stream()
                .map(z -> z.getNome())
                .collect(Collectors.toSet());
    }
}