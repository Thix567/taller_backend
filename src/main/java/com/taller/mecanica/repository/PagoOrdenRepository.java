package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.PagoOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoOrdenRepository extends JpaRepository<PagoOrden, Long> {

    List<PagoOrden> findByInformeIdOrderByFechaPagoAsc(Long informeId);

    Optional<PagoOrden> findByIdAndInformeId(Long id, Long informeId);

    boolean existsByInformeId(Long informeId);

    @Query("select sum(p.monto) from PagoOrden p where p.informe.id = :informeId")
    BigDecimal sumMontoByInformeId(@Param("informeId") Long informeId);

    @Query("select sum(p.monto) from PagoOrden p where p.fechaPago >= :desde and p.fechaPago < :hasta")
    BigDecimal sumMontoEntreFechas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
