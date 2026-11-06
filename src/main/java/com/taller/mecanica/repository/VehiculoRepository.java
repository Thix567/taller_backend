package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCase(String placa);

    boolean existsByPlacaIgnoreCaseAndIdNot(String placa, Long id);

    boolean existsByClienteId(Long clienteId);

    List<Vehiculo> findByClienteIdOrderByPlacaAsc(Long clienteId);

    List<Vehiculo> findAllByOrderByPlacaAsc();

    @Query("""
            select v from Vehiculo v join v.cliente c
            where upper(v.placa) like upper(concat('%', :q, '%'))
               or upper(v.marca) like upper(concat('%', :q, '%'))
               or upper(v.modelo) like upper(concat('%', :q, '%'))
               or upper(c.nombreCompleto) like upper(concat('%', :q, '%'))
            order by v.placa asc
            """)
    List<Vehiculo> buscar(@Param("q") String q);
}
