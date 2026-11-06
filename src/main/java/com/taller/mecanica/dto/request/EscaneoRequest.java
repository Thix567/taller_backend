package com.taller.mecanica.dto.request;

import jakarta.validation.constraints.Size;

public record EscaneoRequest(
        @Size(max = 10000, message = "El escaneo inicial no debe superar 10000 caracteres")
        String escaneoInicialResultado,

        @Size(max = 10000, message = "El escaneo final no debe superar 10000 caracteres")
        String escaneoFinalResultado
) {
}
