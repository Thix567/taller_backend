package com.taller.mecanica.controller;

import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.service.VehiculoService;
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
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping
    public List<Vehiculo> listar(@RequestParam(required = false) Long clienteId,
                                 @RequestParam(required = false) String q) {
        return vehiculoService.listar(clienteId, q);
    }

    @GetMapping("/{id}")
    public Vehiculo obtener(@PathVariable Long id) {
        return vehiculoService.obtener(id);
    }

    @GetMapping("/placa/{placa}")
    public Vehiculo porPlaca(@PathVariable String placa) {
        return vehiculoService.porPlaca(placa);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehiculo crear(@Valid @RequestBody Vehiculo datos) {
        return vehiculoService.crear(datos);
    }

    @PutMapping("/{id}")
    public Vehiculo actualizar(@PathVariable Long id, @Valid @RequestBody Vehiculo datos) {
        return vehiculoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
    }
}
