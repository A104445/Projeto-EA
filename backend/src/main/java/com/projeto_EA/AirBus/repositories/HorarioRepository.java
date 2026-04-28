package com.projeto_EA.AirBus.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projeto_EA.AirBus.models.entities.HorarioEntity;

@Repository
public interface HorarioRepository extends JpaRepository<HorarioEntity, Long> {

    List<HorarioEntity> findByCarreiraId(Long carreiraId);

    List<HorarioEntity> findByParagemId(Long paragoemId);

    List<HorarioEntity> findByCarreiraIdAndParagemId(Long carreiraId, Long paragoemId);

    List<HorarioEntity> findByCarreiraIdAndDiaSemana(Long carreiraId, int diaSemana);

    @Query("""
        SELECT h FROM HorarioEntity h
        WHERE h.paragem.id = :paragemId
          AND h.diaSemana  = :diaSemana
        ORDER BY h.horaChegada ASC
        """)
    List<HorarioEntity> findByParagemAndDia(
        @Param("paragemId") Long paragemId,
        @Param("diaSemana") int diaSemana
    );

    List<HorarioEntity> findByCarreiraIdAndParagemIdAndDiaSemanaOrderByHoraChegada(
        Long carreiraId, Long paragoemId, int diaSemana);
}