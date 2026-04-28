package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.TituloTransporteEntity;
import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TituloTransporteRepository extends JpaRepository<TituloTransporteEntity, Long> {

    List<TituloTransporteEntity> findByUtenteId(Long utenteId);

    List<TituloTransporteEntity> findByUtenteIdAndEstado(Long utenteId, EstadoGeral estado);
}