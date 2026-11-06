package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    List<Mecanico> findByActivoTrueOrderByNombreCompletoAsc();

    List<Mecanico> findAllByOrderByNombreCompletoAsc();
}
