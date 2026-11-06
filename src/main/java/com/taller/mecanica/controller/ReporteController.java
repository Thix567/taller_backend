package com.taller.mecanica.controller;

import com.taller.mecanica.dto.response.DashboardResponse;
import com.taller.mecanica.dto.response.HistorialPlacaResponse;
import com.taller.mecanica.dto.response.ProductividadMecanicoResponse;
import com.taller.mecanica.dto.response.RecordatorioResponse;
import com.taller.mecanica.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return reporteService.dashboard();
    }

    @GetMapping("/historial/{placa}")
    public HistorialPlacaResponse historial(@PathVariable String placa) {
        return reporteService.historial(placa);
    }

    @GetMapping("/recordatorios")
    public List<RecordatorioResponse> recordatorios(@RequestParam(required = false) Integer dias) {
        return reporteService.recordatorios(dias);
    }

    @GetMapping("/mecanicos/productividad")
    public List<ProductividadMecanicoResponse> productividad(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return reporteService.productividad(desde, hasta);
    }
}
