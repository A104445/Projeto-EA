package com.projeto_EA.AirBus.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "viagem")
@Getter @Setter @NoArgsConstructor
public class ViagemEntity {

    public static final String EM_CURSO  = "EM_CURSO";
    public static final String CONCLUIDA = "CONCLUIDA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private UserEntity utente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "titulo_id")
    private TituloTransporteEntity titulo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origem_id")
    private ParagemEntity origem;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private ParagemEntity destino;

    @Column(nullable = false)
    private LocalDateTime dataHoraEntrada;

    private LocalDateTime dataHoraSaida;

    @Column(nullable = false)
    private String estado = EM_CURSO;

    @Column(nullable = false)
    private boolean irregular = false;
}