package com.taller.mecanica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve la aplicacion Angular compilada (src/main/resources/static).
 * Se usa en el perfil "demo" para no necesitar Node en la presentacion.
 */
@Controller
public class SpaController {

    @GetMapping({
            "/",
            "/login",
            "/dashboard",
            "/clientes",
            "/vehiculos",
            "/citas",
            "/ordenes",
            "/ordenes/{id}",
            "/mecanicos",
            "/paquetes",
            "/insumos",
            "/reportes"
    })
    public String index() {
        return "forward:/index.html";
    }
}
