package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "horario")
@Getter @Setter @NoArgsConstructor
public class HorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paragem_id")
    private ParagemEntity paragem;

    @Column(nullable = false)
    private LocalTime horaChegada;

    // 1=Segunda, 2=Terça, ..., 5=Sexta, 6=Sábado, 7=Domingo
    @Column(nullable = false)
    private int diaSemana;

    public HorarioEntity(CarreiraEntity carreira, ParagemEntity paragem, LocalTime horaChegada, int diaSemana) {
        this.carreira = carreira;
        this.paragem = paragem;
        this.horaChegada = horaChegada;
        this.diaSemana = diaSemana;
    }
}