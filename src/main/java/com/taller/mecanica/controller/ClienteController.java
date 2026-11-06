package com.taller.mecanica.controller;

import com.taller.mecanica.model.entity.Cliente;
import com.taller.mecanica.model.entity.Vehiculo;
import com.taller.mecanica.service.ClienteService;
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
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;

    @GetMapping
    public List<Cliente> listar(@RequestParam(required = false) String q) {
        return clienteService.listar(q);
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Long id) {
        return clienteService.obtener(id);
    }

    @GetMapping("/{id}/vehiculos")
    public List<Vehiculo> vehiculos(@PathVariable Long id) {
        clienteService.obtener(id);
        return vehiculoService.listar(id, null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@Valid @RequestBody Cliente datos) {
        return clienteService.crear(datos);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Long id, @Valid @RequestBody Cliente datos) {
        return clienteService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
    }
}
