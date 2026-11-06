package com.taller.mecanica.dto.auth;

public record LoginResponse(
        String token,
        String tipo,
        long expiraEn,
        UsuarioResponse usuario
) {
}
