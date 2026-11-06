package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.NivelCombustible;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record InformeUpdateRequest(
        Long mecanicoId,

        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        Integer kilometrajeIngreso,

        NivelCombustible nivelCombustible,

        @Size(max = 2000, message = "El inventario de recepcion no debe superar 2000 caracteres")
        String inventarioRecepcion,

        @Size(max = 2000, message = "Las observaciones no deben superar 2000 caracteres")
        String observacionesRecepcion,

        @Size(max = 10000, message = "Las recomendaciones no deben superar 10000 caracteres")
        String recomendacionesFuturas,

        @Size(max = 10000, message = "Las observaciones generales no deben superar 10000 caracteres")
        String observacionesGenerales
) {
}
