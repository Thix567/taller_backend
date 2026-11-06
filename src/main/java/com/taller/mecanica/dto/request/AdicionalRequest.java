package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.TipoAdicional;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AdicionalRequest(
        @NotBlank(message = "La descripcion es obligatoria")
        @Size(max = 300, message = "La descripcion no debe superar 300 caracteres")
        String descripcion,

        @NotNull(message = "El tipo de adicional es obligatorio")
        TipoAdicional tipo,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
        BigDecimal precioUnitario,

        Boolean aprobadoPorCliente,

        Boolean cobraArandela
) {
}
