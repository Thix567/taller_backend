package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.entity.Cita;
import com.taller.mecanica.model.enums.EstadoCita;

import java.time.LocalDateTime;

public record CitaResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        String clienteTelefono,
        Long vehiculoId,
        String placa,
        String marcaModelo,
        Long paqueteId,
        String paqueteNombre,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFinCalculada,
        EstadoCita estadoCita,
        String observaciones,
        Long informeId,
        LocalDateTime fechaCreacion
) {

    public static CitaResponse from(Cita cita) {
        String marcaModelo = cita.getVehiculo() == null
                ? null
                : (cita.getVehiculo().getMarca() + " " + cita.getVehiculo().getModelo()).trim();
        return new CitaResponse(
                cita.getId(),
                cita.getCliente() == null ? null : cita.getCliente().getId(),
                cita.getCliente() == null ? null : cita.getCliente().getNombreCompleto(),
                cita.getCliente() == null ? null : cita.getCliente().getTelefonoWhatsapp(),
                cita.getVehiculo() == null ? null : cita.getVehiculo().getId(),
                cita.getVehiculo() == null ? null : cita.getVehiculo().getPlaca(),
                marcaModelo,
                cita.getPaqueteDeseado() == null ? null : cita.getPaqueteDeseado().getId(),
                cita.getPaqueteDeseado() == null ? null : cita.getPaqueteDeseado().getNombre(),
                cita.getFechaHoraInicio(),
                cita.getFechaHoraFinCalculada(),
                cita.getEstadoCita(),
                cita.getObservaciones(),
                cita.getInformeId(),
                cita.getFechaCreacion()
        );
    }
}
