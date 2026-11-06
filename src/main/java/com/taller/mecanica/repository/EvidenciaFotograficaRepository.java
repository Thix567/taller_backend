package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.EvidenciaFotografica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvidenciaFotograficaRepository extends JpaRepository<EvidenciaFotografica, Long> {

    List<EvidenciaFotografica> findByInformeIdOrderByFechaSubidaDesc(Long informeId);

    Optional<EvidenciaFotografica> findByIdAndInformeId(Long id, Long informeId);
}
