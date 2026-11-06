package com.taller.mecanica.config;

import com.taller.mecanica.model.entity.Usuario;
import com.taller.mecanica.model.enums.Rol;
import com.taller.mecanica.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String username;

    @Value("${app.admin.password:admin123}")
    private String password;

    @Value("${app.admin.nombre:Administrador}")
    private String nombre;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setNombreCompleto(nombre);
        usuario.setRol(Rol.ADMIN);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        log.warn("Usuario inicial '{}' creado. Cambie APP_ADMIN_PASSWORD antes de usar en produccion.", username);
    }
}
