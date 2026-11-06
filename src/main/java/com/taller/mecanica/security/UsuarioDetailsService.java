package com.taller.mecanica.security;

import com.taller.mecanica.model.entity.Usuario;
import com.taller.mecanica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities("ROLE_" + usuario.getRol().name())
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}
