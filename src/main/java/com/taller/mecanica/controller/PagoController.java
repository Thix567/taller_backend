package com.taller.mecanica.controller;

import com.taller.mecanica.dto.request.PagoRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.service.PagoService;
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
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/informes/{informeId}/pagos")
    public List<InformeDetalleResponse.Pago> listar(@PathVariable Long informeId) {
        return pagoService.listar(informeId);
    }

    @PostMapping("/informes/{informeId}/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public InformeDetalleResponse.Pago registrar(@PathVariable Long informeId,
                                                 @Valid @RequestBody PagoRequest request) {
        return pagoService.registrar(informeId, request);
    }

    @DeleteMapping("/pagos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
    }
}
