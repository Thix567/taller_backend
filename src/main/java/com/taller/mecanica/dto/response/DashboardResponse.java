package com.taller.mecanica.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long totalOrdenes,
        long ordenesActivas,
        long ordenesRecepcionadas,
        long ordenesEnProceso,
        long ordenesListas,
        long ordenesEntregadasMes,
        long citasHoy,
        long citasPendientes,
        BigDecimal ingresosMes,
        BigDecimal saldoPorCobrar,
        long insumosStockCritico,
        long recordatoriosPendientes,
        List<InformeResumenResponse> ordenesRecientes,
        List<RecordatorioResponse> recordatoriosProximos,
        List<CategoriaConteo> ordenesPorEstado
) {

    public record CategoriaConteo(String etiqueta, long total) {
    }
}
