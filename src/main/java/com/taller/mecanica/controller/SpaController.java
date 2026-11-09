package com.taller.mecanica.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve la aplicacion Angular compilada desde src/main/resources/static.
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
