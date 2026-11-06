package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.entity.PaqueteServicio;
import com.taller.mecanica.model.enums.TipoCombustible;

import java.math.BigDecimal;

public record PaqueteInfo(
        Long id,
        String nombre,
        TipoCombustible tipoCombustible,
        BigDecimal precioBase,
        Integer tiempoEstimadoHoras,
        Integer garantiaKm,
        Integer garantiaMeses,
        Boolean activo
) {

    public static PaqueteInfo from(PaqueteServicio paquete) {
        if (paquete == null) {
            return null;
        }
        return new PaqueteInfo(
                paquete.getId(),
                paquete.getNombre(),
                paquete.getTipoCombustible(),
                paquete.getPrecioBase(),
                paquete.getTiempoEstimadoHoras(),
                paquete.getGarantiaKm(),
                paquete.getGarantiaMeses(),
                paquete.getActivo()
        );
    }
}
