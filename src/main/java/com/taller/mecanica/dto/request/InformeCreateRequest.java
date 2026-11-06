package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.NivelCombustible;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record InformeCreateRequest(
        @NotNull(message = "El vehiculo es obligatorio")
        Long vehiculoId,

        @NotNull(message = "El paquete de servicio es obligatorio")
        Long paqueteId,

        @NotNull(message = "El mecanico responsable es obligatorio")
        Long mecanicoId,

        Long citaId,

        LocalDateTime fechaIngreso,

        @NotNull(message = "El kilometraje de ingreso es obligatorio")
        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        Integer kilometrajeIngreso,

        @NotNull(message = "El nivel de combustible es obligatorio")
        NivelCombustible nivelCombustible,

        @Size(max = 2000, message = "El inventario de recepcion no debe superar 2000 caracteres")
        String inventarioRecepcion,

        @Size(max = 2000, message = "Las observaciones no deben superar 2000 caracteres")
        String observacionesRecepcion,

        @Size(max = 10000, message = "El escaneo inicial no debe superar 10000 caracteres")
        String escaneoInicialResultado,

        @Size(max = 10000, message = "Las observaciones generales no deben superar 10000 caracteres")
        String observacionesGenerales
) {
}
