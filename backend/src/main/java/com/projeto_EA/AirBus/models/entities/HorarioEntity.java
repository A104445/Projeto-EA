package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalTime;

@Entity
@Table(name = "horario")
@Getter @NoArgsConstructor
public class HorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paragem_id")
    private ParagemEntity paragem;

    @Column(nullable = false)
    private LocalTime horaChegada;

    // 1=segunda, 2=terça, ..., 5=sexta, 6=sábado, 7=domingo
    @Column(nullable = false)
    private int diaSemana;

    public HorarioEntity(CarreiraEntity carreira, ParagemEntity paragem, LocalTime horaChegada, int diaSemana) {
        this.carreira = carreira;
        this.paragem = paragem;
        this.horaChegada = horaChegada;
        this.diaSemana = diaSemana;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HorarioEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}