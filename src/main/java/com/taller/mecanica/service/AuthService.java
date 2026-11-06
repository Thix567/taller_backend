package com.taller.mecanica.service;

import com.taller.mecanica.dto.auth.LoginRequest;
import com.taller.mecanica.dto.auth.LoginResponse;
import com.taller.mecanica.dto.auth.UsuarioResponse;
import com.taller.mecanica.model.entity.Usuario;
import com.taller.mecanica.repository.UsuarioRepository;
import com.taller.mecanica.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String token = jwtService.generar(usuario);
        return new LoginResponse(token, "Bearer", jwtService.expiracionSegundos(), UsuarioResponse.from(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse actual(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return UsuarioResponse.from(usuario);
    }
}
