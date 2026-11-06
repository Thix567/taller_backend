package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.enums.EstadoInforme;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InformeResumenResponse(
        Long id,
        String numeroOrden,
        String clienteNombre,
        String clienteTelefono,
        Long vehiculoId,
        String placa,
        String marcaModelo,
        String paqueteNombre,
        String mecanicoNombre,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaEstimadaEntrega,
        LocalDateTime fechaEntregaReal,
        LocalDateTime fechaProximoServicio,
        Integer kilometrajeIngreso,
        Integer kilometrajeProximoServicio,
        EstadoInforme estado,
        BigDecimal totalOrden,
        BigDecimal totalPagado,
        BigDecimal saldoPendiente
) {
}
