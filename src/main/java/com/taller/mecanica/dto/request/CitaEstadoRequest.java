package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.EstadoCita;
import jakarta.validation.constraints.NotNull;

public record CitaEstadoRequest(
        @NotNull(message = "El estado de la cita es obligatorio")
        EstadoCita estadoCita
) {
}
