package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.ZonaGeograficaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZonaGeograficaRepository extends JpaRepository<ZonaGeograficaEntity, Long> {
    Optional<ZonaGeograficaEntity> findByNome(String nome);
}