package com.taller.mecanica.model.entity;

import com.taller.mecanica.model.enums.EstadoInforme;
import com.taller.mecanica.model.enums.NivelCombustible;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "informes_vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class InformeVehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_orden", nullable = false, unique = true, length = 20)
    private String numeroOrden;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paquete_id", nullable = false)
    private PaqueteServicio paquete;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mecanico_id", nullable = false)
    private Mecanico mecanico;

    @Column(name = "cita_id")
    private Long citaId;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_estimada_entrega", nullable = false)
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @NotNull
    @Min(value = 0, message = "El kilometraje de ingreso no puede ser negativo")
    @Column(name = "kilometraje_ingreso", nullable = false)
    private Integer kilometrajeIngreso;

    @Column(name = "kilometraje_proximo_servicio", nullable = false)
    private Integer kilometrajeProximoServicio;

    @Column(name = "fecha_proximo_servicio", nullable = false)
    private LocalDateTime fechaProximoServicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_combustible", nullable = false, length = 20)
    private NivelCombustible nivelCombustible = NivelCombustible.MEDIO;

    @Size(max = 2000)
    @Column(name = "inventario_recepcion", length = 2000)
    private String inventarioRecepcion;

    @Size(max = 2000)
    @Column(name = "observaciones_recepcion", length = 2000)
    private String observacionesRecepcion;

    @Column(name = "escaneo_inicial_resultado", columnDefinition = "TEXT")
    private String escaneoInicialResultado;

    @Column(name = "escaneo_final_resultado", columnDefinition = "TEXT")
    private String escaneoFinalResultado;

    @Column(name = "ficha_tecnica_pdf_url", length = 500)
    private String fichaTecnicaPdfUrl;

    @Column(name = "recomendaciones_futuras", columnDefinition = "TEXT")
    private String recomendacionesFuturas;

    @Column(name = "observaciones_generales", columnDefinition = "TEXT")
    private String observacionesGenerales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoInforme estado = EstadoInforme.RECEPCIONADO;

    @Column(name = "precio_paquete", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPaquete;

    @Column(name = "horas_estimadas", nullable = false)
    private Integer horasEstimadas;

    @Column(name = "garantia_km", nullable = false)
    private Integer garantiaKm;

    @Column(name = "garantia_meses", nullable = false)
    private Integer garantiaMeses;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "informe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleInspeccionInforme> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "informe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ConsumoInsumo> consumos = new ArrayList<>();

    @OneToMany(mappedBy = "informe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EvidenciaFotografica> evidencias = new ArrayList<>();

    @OneToMany(mappedBy = "informe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AdicionalOrden> adicionales = new ArrayList<>();

    @OneToMany(mappedBy = "informe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PagoOrden> pagos = new ArrayList<>();
}
