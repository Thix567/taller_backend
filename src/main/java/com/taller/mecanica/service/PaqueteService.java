package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.PaqueteServicio;
import com.taller.mecanica.model.entity.PlantillaItemPaquete;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.PaqueteServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaqueteService {

    private final PaqueteServicioRepository paqueteRepository;
    private final InformeVehiculoRepository informeRepository;

    public List<PaqueteServicio> listar(Boolean soloActivos) {
        if (Boolean.TRUE.equals(soloActivos)) {
            return paqueteRepository.findByActivoTrueOrderByNombreAsc();
        }
        return paqueteRepository.findAllByOrderByNombreAsc();
    }

    public PaqueteServicio obtener(Long id) {
        return paqueteRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Paquete de servicio", id));
    }

    @Transactional
    public PaqueteServicio crear(PaqueteServicio datos) {
        normalizar(datos);
        if (paqueteRepository.existsByNombreIgnoreCase(datos.getNombre())) {
            throw new BusinessException("Ya existe un paquete con el nombre " + datos.getNombre());
        }
        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new BusinessException("El paquete debe tener al menos un item de plantilla");
        }
        datos.setId(null);
        if (datos.getActivo() == null) {
            datos.setActivo(true);
        }
        for (PlantillaItemPaquete item : datos.getItems()) {
            item.setId(null);
            item.setPaquete(datos);
            if (item.getEsCondicional() == null) {
                item.setEsCondicional(false);
            }
            if (item.getRequiereFichaTecnica() == null) {
                item.setRequiereFichaTecnica(false);
            }
        }
        return paqueteRepository.save(datos);
    }

    @Transactional
    public PaqueteServicio actualizar(Long id, PaqueteServicio datos) {
        PaqueteServicio paquete = obtener(id);
        normalizar(datos);
        if (paqueteRepository.existsByNombreIgnoreCaseAndIdNot(datos.getNombre(), id)) {
            throw new BusinessException("Ya existe otro paquete con el nombre " + datos.getNombre());
        }
        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new BusinessException("El paquete debe tener al menos un item de plantilla");
        }
        paquete.setNombre(datos.getNombre());
        paquete.setTipoCombustible(datos.getTipoCombustible());
        paquete.setPrecioBase(datos.getPrecioBase());
        paquete.setTiempoEstimadoHoras(datos.getTiempoEstimadoHoras());
        paquete.setGarantiaKm(datos.getGarantiaKm());
        paquete.setGarantiaMeses(datos.getGarantiaMeses());
        if (datos.getActivo() != null) {
            paquete.setActivo(datos.getActivo());
        }

        paquete.getItems().clear();
        for (PlantillaItemPaquete item : datos.getItems()) {
            item.setId(null);
            item.setPaquete(paquete);
            if (item.getEsCondicional() == null) {
                item.setEsCondicional(false);
            }
            if (item.getRequiereFichaTecnica() == null) {
                item.setRequiereFichaTecnica(false);
            }
            paquete.getItems().add(item);
        }
        return paqueteRepository.save(paquete);
    }

    @Transactional
    public void eliminar(Long id) {
        PaqueteServicio paquete = obtener(id);
        if (informeRepository.existsByPaqueteId(id)) {
            throw new BusinessException("El paquete tiene ordenes asociadas. Desactívelo en lugar de eliminarlo");
        }
        paqueteRepository.delete(paquete);
    }

    private void normalizar(PaqueteServicio paquete) {
        if (paquete.getNombre() != null) {
            paquete.setNombre(paquete.getNombre().trim());
        }
        if (paquete.getItems() == null) {
            paquete.setItems(new ArrayList<>());
        }
    }
}
