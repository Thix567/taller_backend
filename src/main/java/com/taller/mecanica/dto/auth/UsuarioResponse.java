package com.taller.mecanica.dto.auth;

import com.taller.mecanica.model.entity.Usuario;
import com.taller.mecanica.model.enums.Rol;

public record UsuarioResponse(
        Long id,
        String username,
        String nombreCompleto,
        Rol rol,
        Boolean activo
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.getActivo()
        );
    }
}
