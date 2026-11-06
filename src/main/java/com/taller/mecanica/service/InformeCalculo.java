package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.model.entity.AdicionalOrden;
import com.taller.mecanica.model.entity.DetalleInspeccionInforme;
import com.taller.mecanica.model.entity.PagoOrden;
import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.CategoriaItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public final class InformeCalculo {

    private InformeCalculo() {
    }

    public static LocalDateTime fechaEstimadaEntrega(LocalDateTime ingreso, int horas) {
        return ingreso.plusHours(horas);
    }

    public static int kilometrajeProximo(int kilometrajeIngreso, int garantiaKm) {
        return kilometrajeIngreso + garantiaKm;
    }

    public static LocalDateTime fechaProximo(LocalDateTime ingreso, int meses) {
        return ingreso.plusMonths(meses);
    }

    public static BigDecimal totalAdicionales(List<AdicionalOrden> adicionales) {
        if (adicionales == null) {
            return BigDecimal.ZERO;
        }
        return adicionales.stream()
                .filter(AdicionalOrden::seCobra)
                .map(a -> cero(a.getSubtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal totalNoCobrado(List<AdicionalOrden> adicionales) {
        if (adicionales == null) {
            return BigDecimal.ZERO;
        }
        return adicionales.stream()
                .filter(a -> Boolean.TRUE.equals(a.getAprobadoPorCliente()) && !Boolean.TRUE.equals(a.getCobraArandela()))
                .map(a -> cero(a.getSubtotal()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal totalOrden(BigDecimal precioPaquete, List<AdicionalOrden> adicionales) {
        return cero(precioPaquete).add(totalAdicionales(adicionales));
    }

    public static BigDecimal totalPagado(List<PagoOrden> pagos) {
        if (pagos == null) {
            return BigDecimal.ZERO;
        }
        return pagos.stream()
                .map(p -> cero(p.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal saldoPendiente(BigDecimal totalOrden, BigDecimal totalPagado) {
        return cero(totalOrden).subtract(cero(totalPagado));
    }

    public static BigDecimal subtotal(int cantidad, BigDecimal precioUnitario) {
        return cero(precioUnitario)
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public static void validarCierre(String escaneoFinalResultado, List<DetalleInspeccionInforme> detalles) {
        if (escaneoFinalResultado == null || escaneoFinalResultado.isBlank()) {
            throw new BusinessException(
                    "No se puede cerrar la orden sin registrar el escaneo final (escaneoFinalResultado)");
        }
        if (detalles == null) {
            throw new BusinessException("La orden no tiene el checklist de 19 puntos");
        }
        List<String> cortesiasPendientes = detalles.stream()
                .filter(d -> d.getCategoria() == CategoriaItem.CORTESIA)
                .filter(d -> d.getAccionRealizada() != AccionRealizada.COMPLETADO)
                .map(DetalleInspeccionInforme::getDescripcionTarea)
                .collect(Collectors.toList());
        if (!cortesiasPendientes.isEmpty()) {
            throw new BusinessException("No se puede cerrar la orden. Items de CORTESIA sin COMPLETADO: "
                    + String.join(" | ", cortesiasPendientes));
        }
    }

    private static BigDecimal cero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
