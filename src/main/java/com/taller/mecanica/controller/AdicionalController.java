package com.taller.mecanica.controller;

import com.taller.mecanica.dto.request.AdicionalRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.service.AdicionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdicionalController {

    private final AdicionalService adicionalService;

    @GetMapping("/informes/{informeId}/adicionales")
    public List<InformeDetalleResponse.Adicional> listar(@PathVariable Long informeId) {
        return adicionalService.listar(informeId);
    }

    @PostMapping("/informes/{informeId}/adicionales")
    @ResponseStatus(HttpStatus.CREATED)
    public InformeDetalleResponse.Adicional agregar(@PathVariable Long informeId,
                                                    @Valid @RequestBody AdicionalRequest request) {
        return adicionalService.agregar(informeId, request);
    }

    @PutMapping("/adicionales/{id}")
    public InformeDetalleResponse.Adicional actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody AdicionalRequest request) {
        return adicionalService.actualizar(id, request);
    }

    @PatchMapping("/adicionales/{id}/aprobacion")
    public InformeDetalleResponse.Adicional cambiarAprobacion(@PathVariable Long id,
                                                              @RequestParam boolean aprobado) {
        return adicionalService.cambiarAprobacion(id, aprobado);
    }

    @DeleteMapping("/adicionales/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        adicionalService.eliminar(id);
    }
}
