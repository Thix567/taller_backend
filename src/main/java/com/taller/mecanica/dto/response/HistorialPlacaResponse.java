package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.enums.EstadoInforme;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record HistorialPlacaResponse(
        ClienteInfo cliente,
        VehiculoInfo vehiculo,
        List<Visita> visitas
) {

    public record Visita(
            Long informeId,
            String numeroOrden,
            LocalDateTime fecha,
            String paquete,
            String mecanico,
            Integer kilometraje,
            String aceiteUtilizado,
            String fichaTecnicaPdfUrl,
            List<String> adicionales,
            String recomendaciones,
            BigDecimal total,
            BigDecimal totalPagado,
            BigDecimal saldoPendiente,
            EstadoInforme estado
    ) {
    }
}
