package com.taller.mecanica.security;

import com.taller.mecanica.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.issuer:taller-mecanico-api}")
    private String issuer;

    @Value("${app.jwt.expiration-minutes:480}")
    private long expirationMinutes;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generar(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(usuario.getUsername())
                .claim("roles", List.of(usuario.getRol().name()))
                .claim("nombre", usuario.getNombreCompleto())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long expiracionSegundos() {
        return expirationMinutes * 60;
    }
}
