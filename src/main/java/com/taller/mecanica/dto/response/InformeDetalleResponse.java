package com.taller.mecanica.dto.response;

import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.CategoriaInsumo;
import com.taller.mecanica.model.enums.CategoriaItem;
import com.taller.mecanica.model.enums.EstadoComponente;
import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.model.enums.EtapaEvidencia;
import com.taller.mecanica.model.enums.MedioPago;
import com.taller.mecanica.model.enums.NivelCombustible;
import com.taller.mecanica.model.enums.TipoAdicional;
import com.taller.mecanica.model.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InformeDetalleResponse(
        Long id,
        String numeroOrden,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaEstimadaEntrega,
        LocalDateTime fechaEntregaReal,
        LocalDateTime fechaProximoServicio,
        LocalDateTime fechaCreacion,
        EstadoInforme estado,
        Long citaId,
        NivelCombustible nivelCombustible,
        Integer kilometrajeIngreso,
        Integer kilometrajeProximoServicio,
        String inventarioRecepcion,
        String observacionesRecepcion,
        String escaneoInicialResultado,
        String escaneoFinalResultado,
        String fichaTecnicaPdfUrl,
        String recomendacionesFuturas,
        String observacionesGenerales,
        BigDecimal precioPaquete,
        Integer horasEstimadas,
        Integer garantiaKm,
        Integer garantiaMeses,
        ClienteInfo cliente,
        VehiculoInfo vehiculo,
        MecanicoInfo mecanico,
        PaqueteInfo paquete,
        List<Item> checklist,
        List<Consumo> consumos,
        List<Adicional> adicionales,
        List<Pago> pagos,
        List<Evidencia> evidencias,
        Totales totales,
        TallerInfo taller
) {

    public record Item(
            Long id,
            CategoriaItem categoria,
            String descripcionTarea,
            Boolean esCondicional,
            Boolean requiereFichaTecnica,
            Integer ordenVisualizacion,
            EstadoComponente estadoComponente,
            AccionRealizada accionRealizada,
            String valorMedido,
            String observacionTecnica,
            LocalDateTime fechaActualizacion
    ) {
    }

    public record Consumo(
            Long id,
            Long insumoId,
            String codigo,
            String nombre,
            CategoriaInsumo categoria,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            String urlFichaTecnicaPdf
    ) {
    }

    public record Adicional(
            Long id,
            String descripcion,
            TipoAdicional tipo,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal,
            Boolean aprobadoPorCliente,
            Boolean cobraArandela,
            Boolean seCobra
    ) {
    }

    public record Pago(
            Long id,
            LocalDateTime fechaPago,
            BigDecimal monto,
            TipoPago tipoPago,
            MedioPago medioPago,
            String referencia
    ) {
    }

    public record Evidencia(
            Long id,
            String urlImagen,
            String descripcionFoto,
            EtapaEvidencia etapa,
            LocalDateTime fechaSubida
    ) {
    }

    public record Totales(
            BigDecimal totalPaquete,
            BigDecimal totalAdicionales,
            BigDecimal totalNoCobrado,
            BigDecimal totalOrden,
            BigDecimal totalPagado,
            BigDecimal saldoPendiente
    ) {
    }
}
