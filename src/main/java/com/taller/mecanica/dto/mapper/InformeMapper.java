package com.taller.mecanica.dto.mapper;

import com.taller.mecanica.model.entity.AdicionalOrden;
import com.taller.mecanica.model.entity.ConfiguracionTaller;
import com.taller.mecanica.model.entity.ConsumoInsumo;
import com.taller.mecanica.model.entity.DetalleInspeccionInforme;
import com.taller.mecanica.model.entity.EvidenciaFotografica;
import com.taller.mecanica.model.entity.InformeVehiculo;
import com.taller.mecanica.model.entity.PagoOrden;
import com.taller.mecanica.dto.response.ClienteInfo;
import com.taller.mecanica.dto.response.InformeDetalleResponse;
import com.taller.mecanica.dto.response.InformeResumenResponse;
import com.taller.mecanica.dto.response.MecanicoInfo;
import com.taller.mecanica.dto.response.PaqueteInfo;
import com.taller.mecanica.dto.response.TallerInfo;
import com.taller.mecanica.dto.response.VehiculoInfo;
import com.taller.mecanica.service.InformeCalculo;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class InformeMapper {

    private InformeMapper() {
    }

    public static InformeResumenResponse toResumen(InformeVehiculo informe) {
        BigDecimal total = InformeCalculo.totalOrden(informe.getPrecioPaquete(), informe.getAdicionales());
        BigDecimal pagado = InformeCalculo.totalPagado(informe.getPagos());
        String marcaModelo = (informe.getVehiculo().getMarca() + " " + informe.getVehiculo().getModelo()).trim();
        return new InformeResumenResponse(
                informe.getId(),
                informe.getNumeroOrden(),
                informe.getVehiculo().getCliente().getNombreCompleto(),
                informe.getVehiculo().getCliente().getTelefonoWhatsapp(),
                informe.getVehiculo().getId(),
                informe.getVehiculo().getPlaca(),
                marcaModelo,
                informe.getPaquete().getNombre(),
                informe.getMecanico().getNombreCompleto(),
                informe.getFechaIngreso(),
                informe.getFechaEstimadaEntrega(),
                informe.getFechaEntregaReal(),
                informe.getFechaProximoServicio(),
                informe.getKilometrajeIngreso(),
                informe.getKilometrajeProximoServicio(),
                informe.getEstado(),
                total,
                pagado,
                InformeCalculo.saldoPendiente(total, pagado)
        );
    }

    public static InformeDetalleResponse toDetalle(InformeVehiculo informe, ConfiguracionTaller taller) {
        List<InformeDetalleResponse.Item> checklist = informe.getDetalles().stream()
                .sorted(Comparator.comparing(DetalleInspeccionInforme::getOrdenVisualizacion))
                .map(InformeMapper::toItem)
                .collect(Collectors.toList());
        List<InformeDetalleResponse.Consumo> consumos = informe.getConsumos().stream()
                .map(InformeMapper::toConsumo)
                .collect(Collectors.toList());
        List<InformeDetalleResponse.Adicional> adicionales = informe.getAdicionales().stream()
                .map(InformeMapper::toAdicional)
                .collect(Collectors.toList());
        List<InformeDetalleResponse.Pago> pagos = informe.getPagos().stream()
                .sorted(Comparator.comparing(PagoOrden::getFechaPago))
                .map(InformeMapper::toPago)
                .collect(Collectors.toList());
        List<InformeDetalleResponse.Evidencia> evidencias = informe.getEvidencias().stream()
                .sorted(Comparator.comparing(EvidenciaFotografica::getFechaSubida).reversed())
                .map(InformeMapper::toEvidencia)
                .collect(Collectors.toList());

        BigDecimal totalAdicionales = InformeCalculo.totalAdicionales(informe.getAdicionales());
        BigDecimal totalNoCobrado = InformeCalculo.totalNoCobrado(informe.getAdicionales());
        BigDecimal total = InformeCalculo.totalOrden(informe.getPrecioPaquete(), informe.getAdicionales());
        BigDecimal pagado = InformeCalculo.totalPagado(informe.getPagos());

        return new InformeDetalleResponse(
                informe.getId(),
                informe.getNumeroOrden(),
                informe.getFechaIngreso(),
                informe.getFechaEstimadaEntrega(),
                informe.getFechaEntregaReal(),
                informe.getFechaProximoServicio(),
                informe.getFechaCreacion(),
                informe.getEstado(),
                informe.getCitaId(),
                informe.getNivelCombustible(),
                informe.getKilometrajeIngreso(),
                informe.getKilometrajeProximoServicio(),
                informe.getInventarioRecepcion(),
                informe.getObservacionesRecepcion(),
                informe.getEscaneoInicialResultado(),
                informe.getEscaneoFinalResultado(),
                informe.getFichaTecnicaPdfUrl(),
                informe.getRecomendacionesFuturas(),
                informe.getObservacionesGenerales(),
                informe.getPrecioPaquete(),
                informe.getHorasEstimadas(),
                informe.getGarantiaKm(),
                informe.getGarantiaMeses(),
                ClienteInfo.from(informe.getVehiculo().getCliente()),
                VehiculoInfo.from(informe.getVehiculo()),
                MecanicoInfo.from(informe.getMecanico()),
                PaqueteInfo.from(informe.getPaquete()),
                checklist,
                consumos,
                adicionales,
                pagos,
                evidencias,
                new InformeDetalleResponse.Totales(
                        informe.getPrecioPaquete(),
                        totalAdicionales,
                        totalNoCobrado,
                        total,
                        pagado,
                        InformeCalculo.saldoPendiente(total, pagado)
                ),
                TallerInfo.from(taller)
        );
    }

    public static InformeDetalleResponse.Item toItem(DetalleInspeccionInforme detalle) {
        return new InformeDetalleResponse.Item(
                detalle.getId(),
                detalle.getCategoria(),
                detalle.getDescripcionTarea(),
                detalle.getEsCondicional(),
                detalle.getRequiereFichaTecnica(),
                detalle.getOrdenVisualizacion(),
                detalle.getEstadoComponente(),
                detalle.getAccionRealizada(),
                detalle.getValorMedido(),
                detalle.getObservacionTecnica(),
                detalle.getFechaActualizacion()
        );
    }

    public static InformeDetalleResponse.Consumo toConsumo(ConsumoInsumo consumo) {
        BigDecimal subtotal = InformeCalculo.subtotal(consumo.getCantidad(), consumo.getPrecioUnitario());
        return new InformeDetalleResponse.Consumo(
                consumo.getId(),
                consumo.getInsumo().getId(),
                consumo.getInsumo().getCodigo(),
                consumo.getInsumo().getNombre(),
                consumo.getInsumo().getCategoria(),
                consumo.getCantidad(),
                consumo.getPrecioUnitario(),
                subtotal,
                consumo.getUrlFichaTecnicaPdf()
        );
    }

    public static InformeDetalleResponse.Adicional toAdicional(AdicionalOrden adicional) {
        return new InformeDetalleResponse.Adicional(
                adicional.getId(),
                adicional.getDescripcion(),
                adicional.getTipo(),
                adicional.getCantidad(),
                adicional.getPrecioUnitario(),
                adicional.getSubtotal(),
                adicional.getAprobadoPorCliente(),
                adicional.getCobraArandela(),
                adicional.seCobra()
        );
    }

    public static InformeDetalleResponse.Pago toPago(PagoOrden pago) {
        return new InformeDetalleResponse.Pago(
                pago.getId(),
                pago.getFechaPago(),
                pago.getMonto(),
                pago.getTipoPago(),
                pago.getMedioPago(),
                pago.getReferencia()
        );
    }

    public static InformeDetalleResponse.Evidencia toEvidencia(EvidenciaFotografica evidencia) {
        return new InformeDetalleResponse.Evidencia(
                evidencia.getId(),
                evidencia.getUrlImagen(),
                evidencia.getDescripcionFoto(),
                evidencia.getEtapa(),
                evidencia.getFechaSubida()
        );
    }
}
