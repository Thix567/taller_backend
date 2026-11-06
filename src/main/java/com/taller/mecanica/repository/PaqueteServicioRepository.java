package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.PaqueteServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaqueteServicioRepository extends JpaRepository<PaqueteServicio, Long> {

    Optional<PaqueteServicio> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<PaqueteServicio> findByActivoTrueOrderByNombreAsc();

    List<PaqueteServicio> findAllByOrderByNombreAsc();
}
