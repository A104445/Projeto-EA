package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "paragem")
@Getter @Setter @NoArgsConstructor
public class ParagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String estado = "ATIVO";

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
}