package com.taller.mecanica.controller;

import com.taller.mecanica.model.entity.ConfiguracionTaller;
import com.taller.mecanica.service.ConfiguracionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/configuracion")
@RequiredArgsConstructor
public class ConfiguracionController {

    private final ConfiguracionService configuracionService;

    @GetMapping
    public ConfiguracionTaller obtener() {
        return configuracionService.obtener();
    }

    @PutMapping
    public ConfiguracionTaller actualizar(@Valid @RequestBody ConfiguracionTaller datos) {
        return configuracionService.actualizar(datos);
    }
}
