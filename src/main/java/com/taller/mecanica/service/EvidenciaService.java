package com.taller.mecanica.service;

import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.EvidenciaFotografica;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.enums.EtapaEvidencia;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.mapper.InformeMapper;
import com.taller.mecanica.repository.EvidenciaFotograficaRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvidenciaService {

    private final InformeVehiculoRepository informeRepository;
    private final EvidenciaFotograficaRepository evidenciaRepository;
    private final FileStorageService fileStorageService;

    public List<InformeDetalleResponse.Evidencia> listar(Long informeId) {
        cargarInforme(informeId);
        return evidenciaRepository.findByInformeIdOrderByFechaSubidaDesc(informeId).stream()
                .map(InformeMapper::toEvidencia)
                .toList();
    }

    @Transactional
    public InformeDetalleResponse.Evidencia subir(Long informeId, MultipartFile archivo,
                                                  String descripcion, EtapaEvidencia etapa) {
        InformeVehiculo informe = cargarInforme(informeId);
        InformeService.validarEditable(informe);
        String url = fileStorageService.guardarEvidencia(informeId, archivo);

        EvidenciaFotografica evidencia = new EvidenciaFotografica();
        evidencia.setInforme(informe);
        evidencia.setUrlImagen(url);
        evidencia.setDescripcionFoto(descripcion);
        evidencia.setEtapa(etapa != null ? etapa : EtapaEvidencia.INGRESO);
        evidencia.setFechaSubida(LocalDateTime.now());
        return InformeMapper.toEvidencia(evidenciaRepository.save(evidencia));
    }

    @Transactional
    public void eliminar(Long id) {
        EvidenciaFotografica evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Evidencia fotografica", id));
        InformeService.validarEditable(evidencia.getInforme());
        fileStorageService.eliminar(evidencia.getUrlImagen());
        evidenciaRepository.delete(evidencia);
    }

    private InformeVehiculo cargarInforme(Long informeId) {
        return informeRepository.findById(informeId)
                .orElseThrow(() -> ResourceNotFoundException.de("Informe", informeId));
    }
}
