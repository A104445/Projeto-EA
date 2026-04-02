package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.ParagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagemRepository extends JpaRepository<ParagemEntity, Long> {

    @Query("""
        SELECT p FROM ParagemEntity p
        WHERE p.estado = 'ATIVO'
        ORDER BY (6371 * acos(
            cos(radians(:lat)) * cos(radians(p.latitude)) *
            cos(radians(p.longitude) - radians(:lng)) +
            sin(radians(:lat)) * sin(radians(p.latitude))
        ))
        """)
    List<ParagemEntity> findNearestByCoordinates(@Param("lat") double lat,
                                                  @Param("lng") double lng);

    List<ParagemEntity> findByEstado(String estado);
}