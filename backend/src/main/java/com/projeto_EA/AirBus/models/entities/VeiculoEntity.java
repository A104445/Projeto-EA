package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veiculo")
@Getter @Setter @NoArgsConstructor
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private int capacidade;

    @Column(nullable = false)
    private int ocupacaoAtual = 0;

    @ManyToOne
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    public void incrementarOcupacao() {
        if (ocupacaoAtual < capacidade) ocupacaoAtual++;
    }

    public void decrementarOcupacao() {
        if (ocupacaoAtual > 0) ocupacaoAtual--;
    }

    public VeiculoEntity(String matricula, int capacidade, CarreiraEntity carreira) {
        this.matricula = matricula;
        this.capacidade = capacidade;
        this.carreira = carreira;
    }
}