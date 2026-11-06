package com.taller.mecanica.controller;

import com.taller.mecanica.model.enums.EstadoCita;
import com.taller.mecanica.dto.request.CitaEstadoRequest;
import com.taller.mecanica.dto.request.CitaRequest;
import com.taller.mecanica.dto.request.ConvertirCitaRequest;
import com.taller.mecanica.dto.response.CitaResponse;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public List<CitaResponse> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(required = false) EstadoCita estado) {
        return citaService.listar(desde, hasta, estado);
    }

    @GetMapping("/{id}")
    public CitaResponse obtener(@PathVariable Long id) {
        return citaService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponse crear(@Valid @RequestBody CitaRequest request) {
        return citaService.crear(request);
    }

    @PutMapping("/{id}")
    public CitaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CitaRequest request) {
        return citaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public CitaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CitaEstadoRequest request) {
        return citaService.cambiarEstado(id, request.estadoCita());
    }

    @PostMapping("/{id}/convertir")
    @ResponseStatus(HttpStatus.CREATED)
    public InformeDetalleResponse convertir(@PathVariable Long id,
                                            @Valid @RequestBody ConvertirCitaRequest request) {
        return citaService.convertir(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
    }
}
