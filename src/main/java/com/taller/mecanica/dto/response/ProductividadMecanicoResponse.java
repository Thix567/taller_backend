package com.taller.mecanica.dto.response;

import java.math.BigDecimal;

public record ProductividadMecanicoResponse(
        Long mecanicoId,
        String nombreCompleto,
        String especialidad,
        Boolean activo,
        long totalOrdenes,
        long ordenesEntregadas,
        BigDecimal totalFacturado
) {
}
