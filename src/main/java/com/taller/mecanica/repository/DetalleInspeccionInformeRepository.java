package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.DetalleInspeccionInforme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleInspeccionInformeRepository extends JpaRepository<DetalleInspeccionInforme, Long> {

    List<DetalleInspeccionInforme> findByInformeIdOrderByOrdenVisualizacionAsc(Long informeId);

    Optional<DetalleInspeccionInforme> findByIdAndInformeId(Long id, Long informeId);

    long countByInformeId(Long informeId);
}
