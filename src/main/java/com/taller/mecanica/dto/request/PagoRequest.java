package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.MedioPago;
import com.taller.mecanica.model.enums.TipoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoRequest(
        LocalDateTime fechaPago,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotNull(message = "El tipo de pago es obligatorio")
        TipoPago tipoPago,

        @NotNull(message = "El medio de pago es obligatorio")
        MedioPago medioPago,

        @Size(max = 100, message = "La referencia no debe superar 100 caracteres")
        String referencia
) {
}
