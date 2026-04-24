package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.ViagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViagemRepository extends JpaRepository<ViagemEntity, Long> {

    List<ViagemEntity> findByUtenteIDOrderByDataHoraEntradaDesc(Long utenteID);

    Optional<ViagemEntity> findByUtenteIDAndEstado(Long utenteID, String estado);

    long countByCarreiraIDAndEstado(Long carreiraID, String estado);

    List<ViagemEntity> findByUtenteIDAndIrregularTrue(Long utenteID);
}