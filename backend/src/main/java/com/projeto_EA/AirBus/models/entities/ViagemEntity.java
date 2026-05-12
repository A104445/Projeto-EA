package com.projeto_EA.AirBus.models.entities;

import com.projeto_EA.AirBus.models.enums.EstadoViagem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "viagem", indexes = {
    @Index(name = "idx_viagem_utente_estado",  columnList = "utente_id, estado"),
    @Index(name = "idx_viagem_carreira_estado", columnList = "carreira_id, estado")
})
@Getter @NoArgsConstructor
public class ViagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private UserEntity utente;

    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "titulo_id")
    private TituloTransporteEntity titulo;

    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "carreira_id")
    private CarreiraEntity carreira;

    @Setter @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private VeiculoEntity veiculo;

    @Setter @ManyToOne(optional = false)
    @JoinColumn(name = "origem_id")
    private ParagemEntity origem;

    @Setter @ManyToOne
    @JoinColumn(name = "destino_id")
    private ParagemEntity destino;

    @Setter @Column(nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Setter
    private LocalDateTime dataHoraSaida;

    @Setter @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoViagem estado = EstadoViagem.EM_CURSO;

    @Setter @Column(nullable = false)
    private boolean irregular = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ViagemEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}