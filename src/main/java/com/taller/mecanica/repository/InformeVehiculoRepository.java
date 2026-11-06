package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.enums.EstadoInforme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InformeVehiculoRepository extends JpaRepository<InformeVehiculo, Long>, JpaSpecificationExecutor<InformeVehiculo> {

    Optional<InformeVehiculo> findByNumeroOrden(String numeroOrden);

    List<InformeVehiculo> findByVehiculoIdOrderByFechaIngresoDesc(Long vehiculoId);

    List<InformeVehiculo> findByVehiculoPlacaIgnoreCaseOrderByFechaIngresoDesc(String placa);

    Optional<InformeVehiculo> findTopByVehiculoIdOrderByFechaIngresoDesc(Long vehiculoId);

    List<InformeVehiculo> findAllByOrderByFechaIngresoDesc();

    List<InformeVehiculo> findByFechaIngresoBetween(LocalDateTime desde, LocalDateTime hasta);

    long countByEstado(EstadoInforme estado);

    boolean existsByVehiculoId(Long vehiculoId);

    boolean existsByMecanicoId(Long mecanicoId);

    boolean existsByPaqueteId(Long paqueteId);

    boolean existsByCitaId(Long citaId);

    @Query("select max(i.numeroOrden) from InformeVehiculo i where i.numeroOrden like concat(:prefijo, '%')")
    String ultimoNumero(@Param("prefijo") String prefijo);
}
