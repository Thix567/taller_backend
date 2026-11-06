package com.taller.mecanica.repository;

import com.taller.mecanica.model.entity.Cita;
import com.taller.mecanica.model.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaHoraInicioBetweenOrderByFechaHoraInicioAsc(LocalDateTime desde, LocalDateTime hasta);

    List<Cita> findAllByOrderByFechaHoraInicioDesc();

    long countByEstadoCita(EstadoCita estadoCita);

    boolean existsByVehiculoIdAndEstadoCitaIn(Long vehiculoId, Collection<EstadoCita> estados);

    boolean existsByInformeId(Long informeId);

    Optional<Cita> findByIdAndInformeIdIsNull(Long id);

    @Query("""
            select c from Cita c
            where c.estadoCita <> com.taller.mecanica.model.enums.EstadoCita.CANCELADA
              and c.fechaHoraInicio < :fin
              and c.fechaHoraFinCalculada > :inicio
            """)
    List<Cita> buscarSolapadas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
            select c from Cita c
            where c.estadoCita <> com.taller.mecanica.model.enums.EstadoCita.CANCELADA
              and c.fechaHoraInicio < :fin
              and c.fechaHoraFinCalculada > :inicio
              and c.id <> :excluirId
            """)
    List<Cita> buscarSolapadasExcluyendo(@Param("inicio") LocalDateTime inicio,
                                        @Param("fin") LocalDateTime fin,
                                        @Param("excluirId") Long excluirId);
}
