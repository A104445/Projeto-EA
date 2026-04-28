package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.CarreiraEntity;
import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreiraRepository extends JpaRepository<CarreiraEntity, Long> {

    List<CarreiraEntity> findByEstado(EstadoGeral estado);

    @Query("SELECT c FROM CarreiraEntity c JOIN c.paragens p WHERE p.id = :paragemID AND c.estado = :estado")
    List<CarreiraEntity> findByParagemId(@Param("paragemID") Long paragemID, @Param("estado") EstadoGeral estado);
}