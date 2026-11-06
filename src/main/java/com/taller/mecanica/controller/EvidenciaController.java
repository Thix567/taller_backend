package com.taller.mecanica.controller;

import com.taller.mecanica.model.enums.EtapaEvidencia;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.service.EvidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    @GetMapping("/informes/{informeId}/evidencias")
    public List<InformeDetalleResponse.Evidencia> listar(@PathVariable Long informeId) {
        return evidenciaService.listar(informeId);
    }

    @PostMapping(value = "/informes/{informeId}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public InformeDetalleResponse.Evidencia subir(@PathVariable Long informeId,
                                                  @RequestPart("archivo") MultipartFile archivo,
                                                  @RequestParam(required = false) String descripcion,
                                                  @RequestParam(required = false) EtapaEvidencia etapa) {
        return evidenciaService.subir(informeId, archivo, descripcion, etapa);
    }

    @DeleteMapping("/evidencias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        evidenciaService.eliminar(id);
    }
}
