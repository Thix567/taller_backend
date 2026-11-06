package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.entity.PagoOrden;
import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.dto.request.PagoRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.mapper.InformeMapper;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.PagoOrdenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PagoService {

    private final InformeVehiculoRepository informeRepository;
    private final PagoOrdenRepository pagoRepository;

    public List<InformeDetalleResponse.Pago> listar(Long informeId) {
        cargarInforme(informeId);
        return pagoRepository.findByInformeIdOrderByFechaPagoAsc(informeId).stream()
                .map(InformeMapper::toPago)
                .toList();
    }

    @Transactional
    public InformeDetalleResponse.Pago registrar(Long informeId, PagoRequest request) {
        InformeVehiculo informe = cargarInforme(informeId);
        if (informe.getEstado() == EstadoInforme.CANCELADO) {
            throw new BusinessException("No se pueden registrar pagos de una orden cancelada");
        }
        BigDecimal total = InformeCalculo.totalOrden(informe.getPrecioPaquete(), informe.getAdicionales());
        BigDecimal pagado = InformeCalculo.totalPagado(informe.getPagos());
        BigDecimal saldo = InformeCalculo.saldoPendiente(total, pagado);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La orden ya se encuentra totalmente pagada");
        }
        if (request.monto().compareTo(saldo) > 0) {
            throw new BusinessException("El monto excede el saldo pendiente de S/ " + saldo.setScale(2));
        }

        PagoOrden pago = new PagoOrden();
        pago.setInforme(informe);
        pago.setFechaPago(request.fechaPago() != null ? request.fechaPago() : LocalDateTime.now());
        pago.setMonto(request.monto());
        pago.setTipoPago(request.tipoPago());
        pago.setMedioPago(request.medioPago());
        pago.setReferencia(request.referencia());
        return InformeMapper.toPago(pagoRepository.save(pago));
    }

    @Transactional
    public void eliminar(Long id) {
        PagoOrden pago = pagoRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.de("Pago", id));
        if (pago.getInforme().getEstado() == EstadoInforme.ENTREGADO) {
            throw new BusinessException("No se puede anular un pago de una orden ya entregada");
        }
        pagoRepository.delete(pago);
    }

    private InformeVehiculo cargarInforme(Long informeId) {
        return informeRepository.findById(informeId)
                .orElseThrow(() -> ResourceNotFoundException.de("Informe", informeId));
    }
}
