package com.taller.mecanica.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CitaRequest(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        @NotNull(message = "El vehiculo es obligatorio")
        Long vehiculoId,

        @NotNull(message = "El paquete deseado es obligatorio")
        Long paqueteId,

        @NotNull(message = "La fecha y hora de inicio es obligatoria")
        LocalDateTime fechaHoraInicio,

        @Size(max = 1000, message = "Las observaciones no deben superar 1000 caracteres")
        String observaciones
) {
}
