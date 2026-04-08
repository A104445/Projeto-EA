package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.TituloTransporteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TituloTransporteRepository extends JpaRepository<TituloTransporteEntity, Long> {

    List<TituloTransporteEntity> findByUtenteID(Long utenteID);

    List<TituloTransporteEntity> findByUtenteIDAndEstado(Long utenteID, String estado);
}