package com.projeto_EA.AirBus.models.entities;

import com.projeto_EA.AirBus.models.DTOs.NotificacaoDTO;
import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "titulo_transporte", indexes = {
    @Index(name = "idx_titulo_utente_estado", columnList = "utente_id, estado")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_titulo", discriminatorType = DiscriminatorType.STRING)
@Getter @NoArgsConstructor
public abstract class TituloTransporteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private UserEntity utente;

    @Column(nullable = false)
    private LocalDateTime dataAquisicao;

    @PrePersist
    private void prePersist() {
        this.dataAquisicao = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeral estado = EstadoGeral.ATIVO;

    @ManyToMany
    @JoinTable(
        name = "titulo_zona",
        joinColumns = @JoinColumn(name = "titulo_id"),
        inverseJoinColumns = @JoinColumn(name = "zona_id")
    )
    private Set<ZonaGeograficaEntity> zonas = new HashSet<>();

    public void definirZonas(Set<ZonaGeograficaEntity> novasZonas) {
        this.zonas.clear();
        this.zonas.addAll(novasZonas);
    }

    public abstract boolean isValid();

    public List<NotificacaoDTO> gerarNotificacoes() {
        return List.of();
    }


    public void validarParaCheckIn(CarreiraEntity carreira, ParagemEntity paragem) {
        boolean zonaValid = paragem.getZonas().stream().anyMatch(z -> this.zonas.contains(z));
        if (!zonaValid) {
            throw new IllegalStateException("Título não cobre a zona desta paragem!");
        }
    }


    public boolean isIrregular(Set<ZonaGeograficaEntity> zonasPercorridas) {
        return !this.zonas.containsAll(zonasPercorridas);
    }


    public void aoFinalizarViagem() {
        // no-op por defeito
    }

    public abstract String getTipo();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TituloTransporteEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}