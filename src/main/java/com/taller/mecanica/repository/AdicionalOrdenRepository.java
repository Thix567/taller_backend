package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.AdicionalOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdicionalOrdenRepository extends JpaRepository<AdicionalOrden, Long> {

    List<AdicionalOrden> findByInformeIdOrderByIdAsc(Long informeId);

    Optional<AdicionalOrden> findByIdAndInformeId(Long id, Long informeId);
}
