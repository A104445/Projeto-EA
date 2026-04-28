package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.ViagemEntity;
import com.projeto_EA.AirBus.models.enums.EstadoViagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViagemRepository extends JpaRepository<ViagemEntity, Long> {

    List<ViagemEntity> findByUtenteIdOrderByDataHoraEntradaDesc(Long utenteId);

    Optional<ViagemEntity> findByUtenteIdAndEstado(Long utenteId, EstadoViagem estado);

    long countByCarreiraIdAndEstado(Long carreiraId, EstadoViagem estado);

    @Query("""
        SELECT v FROM ViagemEntity v
        JOIN FETCH v.carreira
        JOIN FETCH v.origem
        LEFT JOIN FETCH v.destino
        WHERE v.utente.id = :utenteID AND v.irregular = true
    """)
    List<ViagemEntity> findIrregularByUtenteIDWithDetails(@Param("utenteID") Long utenteID);
}