package com.projeto_EA.AirBus.repositories;

import com.projeto_EA.AirBus.models.entities.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VeiculoRepository extends JpaRepository<VeiculoEntity, Long> {

    List<VeiculoEntity> findByCarreiraIdOrderById(Long carreiraId);

    // UPDATE atómico: a BD serializa escritas à mesma linha, eliminando race conditions.
    // LEAST/GREATEST garantem os limites [0, capacidade] sem lógica extra no Java.
    @Modifying
    @Query(value = "UPDATE veiculo SET ocupacao_atual = LEAST(ocupacao_atual + 1, capacidade) WHERE id = :id", nativeQuery = true)
    void incrementarOcupacao(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE veiculo SET ocupacao_atual = GREATEST(ocupacao_atual - 1, 0) WHERE id = :id", nativeQuery = true)
    void decrementarOcupacao(@Param("id") Long id);
}