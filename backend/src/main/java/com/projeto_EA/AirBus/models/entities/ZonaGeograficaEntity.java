package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "zona_geografica")
@Getter @Setter @NoArgsConstructor
public class ZonaGeograficaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, unique = true)
    private String nome;

    public ZonaGeograficaEntity(String nome) {
        this.nome = nome;
    }
}