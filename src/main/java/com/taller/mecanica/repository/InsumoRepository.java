package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.Insumo;
import com.taller.mecanica.model.enums.CategoriaInsumo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    Optional<Insumo> findByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    List<Insumo> findAllByOrderByNombreAsc();

    List<Insumo> findByCategoriaOrderByNombreAsc(CategoriaInsumo categoria);

    @Query("select i from Insumo i where i.stockActual <= i.stockMinimo order by i.nombre asc")
    List<Insumo> findConStockBajo();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Insumo i where i.id = :id")
    Optional<Insumo> buscarParaActualizar(@Param("id") Long id);
}
