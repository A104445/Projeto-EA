package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "titulo_transporte")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_titulo", discriminatorType = DiscriminatorType.STRING)
@Getter @Setter @NoArgsConstructor
public abstract class TituloTransporteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private UserEntity utente;

    @Column(nullable = false)
    private LocalDateTime dataAquisicao = LocalDateTime.now();

    @Column(nullable = false)
    private String estado = "ATIVO";

    @ManyToMany
    @JoinTable(
        name = "titulo_zona",
        joinColumns = @JoinColumn(name = "titulo_id"),
        inverseJoinColumns = @JoinColumn(name = "zona_id")
    )
    private Set<ZonaGeograficaEntity> zonas = new HashSet<>();

    public abstract boolean isValid();


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

    public String getTipo() {
        DiscriminatorValue dv = this.getClass().getAnnotation(DiscriminatorValue.class);
        return dv != null ? dv.value() : "DESCONHECIDO";
    }
}