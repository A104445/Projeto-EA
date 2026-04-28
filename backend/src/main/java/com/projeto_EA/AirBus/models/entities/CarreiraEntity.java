package com.projeto_EA.AirBus.models.entities;

import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carreira")
@Getter @NoArgsConstructor
public class CarreiraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String num;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeral estado = EstadoGeral.ATIVO;

    @ManyToMany
    @JoinTable(
        name = "carreira_paragem",
        joinColumns = @JoinColumn(name = "carreira_id"),
        inverseJoinColumns = @JoinColumn(name = "paragem_id")
    )
    @OrderColumn(name = "ordem")
    @BatchSize(size = 20)
    private List<ParagemEntity> paragens = new ArrayList<>();

    @OneToMany(mappedBy = "carreira")
    @BatchSize(size = 20)
    private List<VeiculoEntity> veiculos = new ArrayList<>();

    public CarreiraEntity(String num, String nome) {
        this.num = num;
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarreiraEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}