package com.taller.mecanica.controller;

import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.dto.request.ChecklistUpdateRequest;
import com.taller.mecanica.dto.request.EscaneoRequest;
import com.taller.mecanica.dto.request.EstadoInformeRequest;
import com.taller.mecanica.dto.request.InformeCreateRequest;
import com.taller.mecanica.dto.request.InformeUpdateRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.response.InformeResumenResponse;
import com.taller.mecanica.service.InformeService;
import com.taller.mecanica.service.ReporteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/informes")
@RequiredArgsConstructor
public class InformeController {

    private final InformeService informeService;
    private final ReporteService reporteService;

    @GetMapping
    public List<InformeResumenResponse> listar(
            @RequestParam(required = false) EstadoInforme estado,
            @RequestParam(required = false) Long mecanicoId,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return informeService.listar(estado, mecanicoId, placa, desde, hasta);
    }

    @GetMapping("/{id}")
    public InformeDetalleResponse obtener(@PathVariable Long id) {
        return informeService.obtenerDetalle(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InformeDetalleResponse crear(@Valid @RequestBody InformeCreateRequest request) {
        return informeService.crear(request);
    }

    @PutMapping("/{id}")
    public InformeDetalleResponse actualizar(@PathVariable Long id,
                                             @Valid @RequestBody InformeUpdateRequest request) {
        return informeService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public InformeDetalleResponse cambiarEstado(@PathVariable Long id,
                                                @Valid @RequestBody EstadoInformeRequest request) {
        return informeService.cambiarEstado(id, request.estado());
    }

    @PatchMapping("/{id}/escaneos")
    public InformeDetalleResponse actualizarEscaneos(@PathVariable Long id,
                                                     @Valid @RequestBody EscaneoRequest request) {
        return informeService.actualizarEscaneos(id, request);
    }

    @GetMapping("/{id}/checklist")
    public List<InformeDetalleResponse.Item> checklist(@PathVariable Long id) {
        return informeService.listarChecklist(id);
    }

    @PatchMapping("/{id}/checklist/{detalleId}")
    public InformeDetalleResponse.Item actualizarChecklist(@PathVariable Long id,
                                                           @PathVariable Long detalleId,
                                                           @Valid @RequestBody ChecklistUpdateRequest request) {
        return informeService.actualizarChecklist(id, detalleId, request);
    }

    @GetMapping("/{id}/reporte")
    public Map<String, Object> reporte(@PathVariable Long id) {
        InformeDetalleResponse detalle = informeService.obtenerDetalle(id);
        return Map.of(
                "informe", detalle,
                "generadoEn", LocalDateTime.now().toString(),
                "historialVehiculo", reporteService.historial(detalle.vehiculo().placa())
        );
    }

    @PostMapping(value = "/{id}/ficha-tecnica", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InformeDetalleResponse subirFichaTecnica(@PathVariable Long id,
                                                    @RequestPart("archivo") MultipartFile archivo) {
        return informeService.subirFichaTecnica(id, archivo);
    }
}
