package com.taller.mecanica.dto.response;

import java.time.LocalDateTime;

public record RecordatorioResponse(
        Long informeId,
        Long clienteId,
        String clienteNombre,
        String telefonoWhatsapp,
        Long vehiculoId,
        String placa,
        String marcaModelo,
        LocalDateTime ultimoServicio,
        Integer kilometrajeIngreso,
        Integer kilometrajeActual,
        Integer kilometrajeProximoServicio,
        LocalDateTime fechaProximoServicio,
        Long diasRestantes,
        Integer kmRestantes,
        String motivo,
        String estadoAlerta,
        String whatsappUrl,
        String mensaje
) {
}
