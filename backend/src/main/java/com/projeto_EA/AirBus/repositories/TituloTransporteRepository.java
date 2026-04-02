package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.TituloTransporteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TituloTransporteRepository extends JpaRepository<TituloTransporteEntity, Long> {

    List<TituloTransporteEntity> findByUtenteID(Long utenteID);

    @Query("SELECT t FROM TituloTransporteEntity t WHERE t.utente.id = :utenteID AND t.estado = 'ATIVO'")
    List<TituloTransporteEntity> findActivosByUtenteID(@Param("utenteID") Long utenteID);
}