package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.CarreiraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarreiraRepository extends JpaRepository<CarreiraEntity, Long> {

    List<CarreiraEntity> findByEstado(String estado);

    @Query("SELECT c FROM CarreiraEntity c JOIN c.paragens p WHERE p.id = :paragemID AND c.estado = 'ATIVO'")
    List<CarreiraEntity> findByParagemID(@Param("paragemID") Long paragemID);
}