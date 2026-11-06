package com.taller.mecanica.controller;

import com.taller.mecanica.dto.auth.LoginRequest;
import com.taller.mecanica.dto.auth.LoginResponse;
import com.taller.mecanica.dto.auth.UsuarioResponse;
import com.taller.mecanica.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Inicio de sesion y datos del usuario autenticado")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion y obtener un token JWT")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Obtener el usuario autenticado")
    public UsuarioResponse me(Authentication authentication) {
        return authService.actual(authentication.getName());
    }
}
