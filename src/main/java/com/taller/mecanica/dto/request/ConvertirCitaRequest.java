package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.NivelCombustible;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConvertirCitaRequest(
        @NotNull(message = "El mecanico responsable es obligatorio")
        Long mecanicoId,

        @NotNull(message = "El kilometraje de ingreso es obligatorio")
        @Min(value = 0, message = "El kilometraje no puede ser negativo")
        Integer kilometrajeIngreso,

        @NotNull(message = "El nivel de combustible es obligatorio")
        NivelCombustible nivelCombustible,

        @Size(max = 2000, message = "El inventario de recepcion no debe superar 2000 caracteres")
        String inventarioRecepcion,

        @Size(max = 2000, message = "Las observaciones no deben superar 2000 caracteres")
        String observacionesRecepcion
) {
}
