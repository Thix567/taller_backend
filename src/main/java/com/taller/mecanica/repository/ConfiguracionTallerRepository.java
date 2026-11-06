package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.ConfiguracionTaller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionTallerRepository extends JpaRepository<ConfiguracionTaller, Long> {

    Optional<ConfiguracionTaller> findFirstByOrderByIdAsc();
}
