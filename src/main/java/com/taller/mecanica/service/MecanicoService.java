package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.Mecanico;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.MecanicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MecanicoService {

    private final MecanicoRepository mecanicoRepository;
    private final InformeVehiculoRepository informeRepository;

    public List<Mecanico> listar(Boolean soloActivos) {
        if (Boolean.TRUE.equals(soloActivos)) {
            return mecanicoRepository.findByActivoTrueOrderByNombreCompletoAsc();
        }
        return mecanicoRepository.findAllByOrderByNombreCompletoAsc();
    }

    public Mecanico obtener(Long id) {
        return mecanicoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Mecanico", id));
    }

    @Transactional
    public Mecanico crear(Mecanico datos) {
        normalizar(datos);
        if (mecanicoRepository.existsByDni(datos.getDni())) {
            throw new BusinessException("Ya existe un mecanico con el DNI " + datos.getDni());
        }
        datos.setId(null);
        if (datos.getActivo() == null) {
            datos.setActivo(true);
        }
        return mecanicoRepository.save(datos);
    }

    @Transactional
    public Mecanico actualizar(Long id, Mecanico datos) {
        Mecanico mecanico = obtener(id);
        normalizar(datos);
        if (mecanicoRepository.existsByDniAndIdNot(datos.getDni(), id)) {
            throw new BusinessException("Ya existe otro mecanico con el DNI " + datos.getDni());
        }
        mecanico.setNombreCompleto(datos.getNombreCompleto());
        mecanico.setDni(datos.getDni());
        mecanico.setEspecialidad(datos.getEspecialidad());
        mecanico.setTelefono(datos.getTelefono());
        if (datos.getActivo() != null) {
            mecanico.setActivo(datos.getActivo());
        }
        return mecanicoRepository.save(mecanico);
    }

    @Transactional
    public void eliminar(Long id) {
        Mecanico mecanico = obtener(id);
        if (informeRepository.existsByMecanicoId(id)) {
            throw new BusinessException("El mecanico tiene ordenes asociadas. Desactívelo en lugar de eliminarlo");
        }
        mecanicoRepository.delete(mecanico);
    }

    private void normalizar(Mecanico mecanico) {
        if (mecanico.getNombreCompleto() != null) {
            mecanico.setNombreCompleto(mecanico.getNombreCompleto().trim());
        }
        if (mecanico.getDni() != null) {
            mecanico.setDni(mecanico.getDni().trim());
        }
    }
}
