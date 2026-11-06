package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.EstadoInforme;
import jakarta.validation.constraints.NotNull;

public record EstadoInformeRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoInforme estado
) {
}
