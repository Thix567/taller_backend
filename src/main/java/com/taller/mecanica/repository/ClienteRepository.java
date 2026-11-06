package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDniRuc(String dniRuc);

    boolean existsByDniRuc(String dniRuc);

    boolean existsByDniRucAndIdNot(String dniRuc, Long id);

    @Query("""
            select c from Cliente c
            where upper(c.nombreCompleto) like upper(concat('%', :q, '%'))
               or upper(c.dniRuc) like upper(concat('%', :q, '%'))
               or c.telefonoWhatsapp like concat('%', :q, '%')
            order by c.nombreCompleto asc
            """)
    List<Cliente> buscar(@Param("q") String q);
}
