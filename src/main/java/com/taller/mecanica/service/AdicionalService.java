package com.taller.mecanica.service;

import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.AdicionalOrden;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.dto.request.AdicionalRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.mapper.InformeMapper;
import com.taller.mecanica.repository.AdicionalOrdenRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdicionalService {

    private final InformeVehiculoRepository informeRepository;
    private final AdicionalOrdenRepository adicionalRepository;

    public List<InformeDetalleResponse.Adicional> listar(Long informeId) {
        cargarInforme(informeId);
        return adicionalRepository.findByInformeIdOrderByIdAsc(informeId).stream()
                .map(InformeMapper::toAdicional)
                .toList();
    }

    @Transactional
    public InformeDetalleResponse.Adicional agregar(Long informeId, AdicionalRequest request) {
        InformeVehiculo informe = cargarInforme(informeId);
        InformeService.validarEditable(informe);
        AdicionalOrden adicional = new AdicionalOrden();
        adicional.setInforme(informe);
        aplicar(adicional, request);
        return InformeMapper.toAdicional(adicionalRepository.save(adicional));
    }

    @Transactional
    public InformeDetalleResponse.Adicional actualizar(Long id, AdicionalRequest request) {
        AdicionalOrden adicional = cargar(id);
        InformeService.validarEditable(adicional.getInforme());
        aplicar(adicional, request);
        return InformeMapper.toAdicional(adicionalRepository.save(adicional));
    }

    @Transactional
    public InformeDetalleResponse.Adicional cambiarAprobacion(Long id, boolean aprobado) {
        AdicionalOrden adicional = cargar(id);
        InformeService.validarEditable(adicional.getInforme());
        adicional.setAprobadoPorCliente(aprobado);
        return InformeMapper.toAdicional(adicionalRepository.save(adicional));
    }

    @Transactional
    public void eliminar(Long id) {
        AdicionalOrden adicional = cargar(id);
        InformeService.validarEditable(adicional.getInforme());
        adicionalRepository.delete(adicional);
    }

    private void aplicar(AdicionalOrden adicional, AdicionalRequest request) {
        adicional.setDescripcion(request.descripcion().trim());
        adicional.setTipo(request.tipo());
        adicional.setCantidad(request.cantidad());
        adicional.setPrecioUnitario(request.precioUnitario());
        adicional.setSubtotal(InformeCalculo.subtotal(request.cantidad(), request.precioUnitario()));
        adicional.setAprobadoPorCliente(Boolean.TRUE.equals(request.aprobadoPorCliente()));
        adicional.setCobraArandela(request.cobraArandela() == null || request.cobraArandela());
    }

    private AdicionalOrden cargar(Long id) {
        return adicionalRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Adicional", id));
    }

    private InformeVehiculo cargarInforme(Long informeId) {
        return informeRepository.findById(informeId)
                .orElseThrow(() -> ResourceNotFoundException.de("Informe", informeId));
    }
}
