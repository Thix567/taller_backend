package com.taller.mecanica.controller;

import com.taller.mecanica.model.entity.Mecanico;
import com.taller.mecanica.service.MecanicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
@RequiredArgsConstructor
public class MecanicoController {

    private final MecanicoService mecanicoService;

    @GetMapping
    public List<Mecanico> listar(@RequestParam(required = false) Boolean soloActivos) {
        return mecanicoService.listar(soloActivos);
    }

    @GetMapping("/{id}")
    public Mecanico obtener(@PathVariable Long id) {
        return mecanicoService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mecanico crear(@Valid @RequestBody Mecanico datos) {
        return mecanicoService.crear(datos);
    }

    @PutMapping("/{id}")
    public Mecanico actualizar(@PathVariable Long id, @Valid @RequestBody Mecanico datos) {
        return mecanicoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        mecanicoService.eliminar(id);
    }
}
