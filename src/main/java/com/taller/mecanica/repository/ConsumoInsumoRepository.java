package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.ConsumoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsumoInsumoRepository extends JpaRepository<ConsumoInsumo, Long> {

    List<ConsumoInsumo> findByInformeIdOrderByIdAsc(Long informeId);

    Optional<ConsumoInsumo> findByIdAndInformeId(Long id, Long informeId);

    boolean existsByInsumoId(Long insumoId);
}
