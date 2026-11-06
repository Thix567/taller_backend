package com.taller.mecanica.dto.request;

import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.EstadoComponente;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChecklistUpdateRequest(
        @NotNull(message = "El estado del componente es obligatorio")
        EstadoComponente estadoComponente,

        @NotNull(message = "La accion realizada es obligatoria")
        AccionRealizada accionRealizada,

        @Size(max = 250, message = "El valor medido no debe superar 250 caracteres")
        String valorMedido,

        @Size(max = 10000, message = "La observacion tecnica no debe superar 10000 caracteres")
        String observacionTecnica
) {
}
