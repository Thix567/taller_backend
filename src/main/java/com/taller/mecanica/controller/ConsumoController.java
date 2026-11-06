package com.taller.mecanica.controller;

import com.taller.mecanica.dto.request.ConsumoInsumoRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.service.ConsumoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsumoController {

    private final ConsumoService consumoService;

    @GetMapping("/informes/{informeId}/consumos")
    public List<InformeDetalleResponse.Consumo> listar(@PathVariable Long informeId) {
        return consumoService.listar(informeId);
    }

    @PostMapping("/informes/{informeId}/consumos")
    @ResponseStatus(HttpStatus.CREATED)
    public InformeDetalleResponse.Consumo agregar(@PathVariable Long informeId,
                                                  @Valid @RequestBody ConsumoInsumoRequest request) {
        return consumoService.agregar(informeId, request);
    }

    @DeleteMapping("/consumos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        consumoService.eliminar(id);
    }
}
