package com.taller.mecanica.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tallerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Taller Mecanico")
                        .version("1.0.0")
                        .description("API REST para la gestion de ordenes, citas, clientes, vehiculos, "
                                + "inventario y reportes de un taller mecanico. Autenticacion con JWT Bearer."))
                .servers(List.of(new Server().url("http://localhost:8080").description("Desarrollo local")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
