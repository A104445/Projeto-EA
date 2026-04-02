package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.HorarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioEntity, Long> {

    List<HorarioEntity> findByCarreiraID(Long carreiraID);

    List<HorarioEntity> findByParagemID(Long paragemID);

    List<HorarioEntity> findByCarreiraIDAndParagemID(Long carreiraID, Long paragemID);

    List<HorarioEntity> findByCarreiraIDAndDiaSemana(Long carreiraID, int diaSemana);
}