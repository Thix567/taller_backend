package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.model.enums.TipoCombustible;

public record VehiculoInfo(
        Long id,
        String placa,
        String marca,
        String modelo,
        Integer anio,
        TipoCombustible tipoCombustible,
        String vinMotor,
        String color,
        Integer kilometrajeActual
) {

    public static VehiculoInfo from(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }
        return new VehiculoInfo(
                vehiculo.getId(),
                vehiculo.getPlaca(),
                vehiculo.getMarca(),
                vehiculo.getModelo(),
                vehiculo.getAnio(),
                vehiculo.getTipoCombustible(),
                vehiculo.getVinMotor(),
                vehiculo.getColor(),
                vehiculo.getKilometrajeActual()
        );
    }
}
