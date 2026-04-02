package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carreira")
@Getter @Setter @NoArgsConstructor
public class CarreiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private String num;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String estado = "ATIVO";

    @ManyToMany
    @JoinTable(
        name = "carreira_paragem",
        joinColumns = @JoinColumn(name = "carreira_id"),
        inverseJoinColumns = @JoinColumn(name = "paragem_id")
    )
    @OrderColumn(name = "ordem")
    private List<ParagemEntity> paragens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private VeiculoEntity veiculo;

    public CarreiraEntity(String num, String nome) {
        this.num = num;
        this.nome = nome;
    }
}