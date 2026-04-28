package com.projeto_EA.AirBus.models.entities;

import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "paragem")
@Getter @NoArgsConstructor
public class ParagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeral estado = EstadoGeral.ATIVO;

    @Column(nullable = false, unique = true)
    private String qr;

    @ManyToMany
    @JoinTable(
        name = "paragem_zona",
        joinColumns = @JoinColumn(name = "paragem_id"),
        inverseJoinColumns = @JoinColumn(name = "zona_id")
    )
    private Set<ZonaGeograficaEntity> zonas = new HashSet<>();

    @ManyToMany(mappedBy = "paragens")
    private List<CarreiraEntity> carreiras = new ArrayList<>();

    public ParagemEntity(String nome, String qr) {
        this.nome = nome;
        this.qr = qr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParagemEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}