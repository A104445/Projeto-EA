package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "veiculo", indexes = {
    @Index(name = "idx_veiculo_carreira", columnList = "carreira_id")
})
@Getter @NoArgsConstructor
public class VeiculoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private Long version = 0L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VeiculoEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}