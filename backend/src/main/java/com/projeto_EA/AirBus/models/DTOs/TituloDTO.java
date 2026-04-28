package com.projeto_EA.AirBus.models.DTOs;

import com.projeto_EA.AirBus.models.entities.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public abstract class TituloDTO {

    private final Long id;
    private final String tipo;
    private final String estado;
    private final LocalDateTime dataAquisicao;
    private final boolean valido;
    private final Set<String> zonas;

    protected TituloDTO(TituloTransporteEntity t) {
        this.id = t.getId();
        this.tipo = t.getTipo();
        this.estado = t.getEstado().name();
        this.dataAquisicao = t.getDataAquisicao();
        this.valido = t.isValid();
        this.zonas = t.getZonas().stream()
                .map(ZonaGeograficaEntity::getNome)
                .collect(Collectors.toSet());
    }

    public static TituloDTO from(TituloTransporteEntity t) {
        if (t instanceof PasseAnualEntity p)       return new PasseAnualDTO(p);
        if (t instanceof BilheteIndividualEntity b) return new BilheteIndividualDTO(b);
        if (t instanceof PacoteViagensEntity pv)   return new PacoteViagensDTO(pv);
        throw new IllegalStateException("Tipo de título desconhecido: " + t.getClass().getSimpleName());
    }
}
