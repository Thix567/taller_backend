package com.taller.mecanica.service;

import com.taller.mecanica.exception.BusinessException;
import com.taller.mecanica.model.entity.AdicionalOrden;
import com.taller.mecanica.model.entity.DetalleInspeccionInforme;
import com.taller.mecanica.model.entity.PagoOrden;
import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.CategoriaItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InformeCalculoTest {

    @Test
    void calculaSlaYProximoServicio() {
        LocalDateTime ingreso = LocalDateTime.of(2026, 1, 10, 9, 0);

        assertEquals(LocalDateTime.of(2026, 1, 10, 14, 0),
                InformeCalculo.fechaEstimadaEntrega(ingreso, 5));
        assertEquals(55_000, InformeCalculo.kilometrajeProximo(50_000, 5_000));
        assertEquals(ingreso.plusMonths(6), InformeCalculo.fechaProximo(ingreso, 6));
    }

    @Test
    void totalOrdenSumaSoloAdicionalesAprobadosYCobrables() {
        AdicionalOrden aprobado = adicional("Filtro de aire", new BigDecimal("45.00"), true, true);
        AdicionalOrden noAprobado = adicional("Bujia", new BigDecimal("80.00"), false, true);
        AdicionalOrden cortesia = adicional("Arandela de carter", new BigDecimal("5.00"), true, false);

        BigDecimal total = InformeCalculo.totalOrden(
                new BigDecimal("550.00"),
                List.of(aprobado, noAprobado, cortesia));

        assertEquals(0, total.compareTo(new BigDecimal("595.00")));
        assertEquals(0, InformeCalculo.totalNoCobrado(List.of(aprobado, noAprobado, cortesia))
                .compareTo(new BigDecimal("5.00")));
    }

    @Test
    void saldoDescuentaLosPagosRegistrados() {
        PagoOrden adelanto = new PagoOrden();
        adelanto.setMonto(new BigDecimal("200.00"));
        PagoOrden liquidacion = new PagoOrden();
        liquidacion.setMonto(new BigDecimal("350.00"));

        BigDecimal pagado = InformeCalculo.totalPagado(List.of(adelanto, liquidacion));
        BigDecimal saldo = InformeCalculo.saldoPendiente(new BigDecimal("550.00"), pagado);

        assertEquals(0, pagado.compareTo(new BigDecimal("550.00")));
        assertEquals(0, saldo.compareTo(BigDecimal.ZERO));
    }

    @Test
    void cierreExigeEscaneoFinalYCortesiasCompletadas() {
        DetalleInspeccionInforme pulverizado = detalle(CategoriaItem.CORTESIA, AccionRealizada.PENDIENTE);
        assertThrows(BusinessException.class,
                () -> InformeCalculo.validarCierre("Sin codigos DTC", List.of(pulverizado)));
        assertThrows(BusinessException.class,
                () -> InformeCalculo.validarCierre("  ", List.of(detalle(CategoriaItem.CORTESIA, AccionRealizada.COMPLETADO))));

        pulverizado.setAccionRealizada(AccionRealizada.COMPLETADO);
        assertDoesNotThrow(() -> InformeCalculo.validarCierre("Sin codigos DTC", List.of(pulverizado)));
    }

    private AdicionalOrden adicional(String descripcion, BigDecimal subtotal, boolean aprobado, boolean cobra) {
        AdicionalOrden adicional = new AdicionalOrden();
        adicional.setDescripcion(descripcion);
        adicional.setSubtotal(subtotal);
        adicional.setAprobadoPorCliente(aprobado);
        adicional.setCobraArandela(cobra);
        return adicional;
    }

    private DetalleInspeccionInforme detalle(CategoriaItem categoria, AccionRealizada accion) {
        DetalleInspeccionInforme detalle = new DetalleInspeccionInforme();
        detalle.setCategoria(categoria);
        detalle.setDescripcionTarea("Item de prueba");
        detalle.setAccionRealizada(accion);
        return detalle;
    }
}
