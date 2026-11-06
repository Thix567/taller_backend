package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.exception.ResourceNotFoundException;
import com.taller.mecanica.model.entity.ConsumoInsumo;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.entity.Insumo;
import com.taller.mecanica.model.enums.CategoriaInsumo;
import com.taller.mecanica.dto.request.ConsumoInsumoRequest;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.mapper.InformeMapper;
import com.taller.mecanica.repository.ConsumoInsumoRepository;
import com.taller.mecanica.repository.InformeVehiculoRepository;
import com.taller.mecanica.repository.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsumoService {

    private final InformeVehiculoRepository informeRepository;
    private final ConsumoInsumoRepository consumoRepository;
    private final InsumoRepository insumoRepository;

    public List<InformeDetalleResponse.Consumo> listar(Long informeId) {
        cargarInforme(informeId);
        return consumoRepository.findByInformeIdOrderByIdAsc(informeId).stream()
                .map(InformeMapper::toConsumo)
                .toList();
    }

    @Transactional
    public InformeDetalleResponse.Consumo agregar(Long informeId, ConsumoInsumoRequest request) {
        InformeVehiculo informe = cargarInforme(informeId);
        InformeService.validarEditable(informe);

        Insumo insumo = insumoRepository.buscarParaActualizar(request.insumoId())
                .orElseThrow(() -> ResourceNotFoundException.de("Insumo", request.insumoId()));
        if (insumo.getStockActual() < request.cantidad()) {
            throw new BusinessException("Stock insuficiente de '" + insumo.getNombre()
                    + "'. Disponible: " + insumo.getStockActual());
        }
        insumo.setStockActual(insumo.getStockActual() - request.cantidad());
        insumoRepository.save(insumo);

        ConsumoInsumo consumo = new ConsumoInsumo();
        consumo.setInforme(informe);
        consumo.setInsumo(insumo);
        consumo.setCantidad(request.cantidad());
        consumo.setPrecioUnitario(insumo.getPrecioVenta());
        consumo.setUrlFichaTecnicaPdf(insumo.getUrlFichaTecnicaPdf());
        ConsumoInsumo guardado = consumoRepository.save(consumo);

        if (insumo.getCategoria() == CategoriaInsumo.ACEITE
                && (informe.getFichaTecnicaPdfUrl() == null || informe.getFichaTecnicaPdfUrl().isBlank())) {
            informe.setFichaTecnicaPdfUrl(insumo.getUrlFichaTecnicaPdf());
            informeRepository.save(informe);
        }
        return InformeMapper.toConsumo(guardado);
    }

    @Transactional
    public void eliminar(Long consumoId) {
        ConsumoInsumo consumo = consumoRepository.findById(consumoId)
                .orElseThrow(() -> ResourceNotFoundException.de("Consumo de insumo", consumoId));
        InformeVehiculo informe = consumo.getInforme();
        InformeService.validarEditable(informe);

        Insumo insumo = insumoRepository.buscarParaActualizar(consumo.getInsumo().getId())
                .orElseThrow(() -> ResourceNotFoundException.de("Insumo", consumo.getInsumo().getId()));
        insumo.setStockActual(insumo.getStockActual() + consumo.getCantidad());
        insumoRepository.save(insumo);
        consumoRepository.delete(consumo);
    }

    private InformeVehiculo cargarInforme(Long informeId) {
        return informeRepository.findById(informeId)
                .orElseThrow(() -> ResourceNotFoundException.de("Informe", informeId));
    }
}
