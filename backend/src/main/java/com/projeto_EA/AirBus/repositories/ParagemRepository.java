package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.ParagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParagemRepository extends JpaRepository<ParagemEntity, Long> {

    Optional<ParagemEntity> findByQr(String qr);

    List<ParagemEntity> findByEstado(String estado);
}