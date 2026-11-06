package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.entity.Cliente;

public record ClienteInfo(
        Long id,
        String nombreCompleto,
        String dniRuc,
        String telefonoWhatsapp,
        String correo,
        String direccion
) {

    public static ClienteInfo from(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return new ClienteInfo(
                cliente.getId(),
                cliente.getNombreCompleto(),
                cliente.getDniRuc(),
                cliente.getTelefonoWhatsapp(),
                cliente.getCorreo(),
                cliente.getDireccion()
        );
    }
}
