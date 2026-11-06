package com.taller.mecanica.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taller.mecanica.model.enums.AccionRealizada;
import com.taller.mecanica.model.enums.CategoriaItem;
import com.taller.mecanica.model.enums.EstadoComponente;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "detalles_inspeccion_informe")
@Getter
@Setter
@NoArgsConstructor
public class DetalleInspeccionInforme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informe_id", nullable = false)
    @JsonIgnore
    private InformeVehiculo informe;

    @Column(name = "plantilla_item_id")
    private Long plantillaItemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaItem categoria;

    @Column(name = "descripcion_tarea", nullable = false, length = 300)
    private String descripcionTarea;

    @Column(name = "es_condicional", nullable = false)
    private Boolean esCondicional = false;

    @Column(name = "requiere_ficha_tecnica", nullable = false)
    private Boolean requiereFichaTecnica = false;

    @Column(name = "orden_visualizacion", nullable = false)
    private Integer ordenVisualizacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_componente", length = 20)
    private EstadoComponente estadoComponente;

    @Enumerated(EnumType.STRING)
    @Column(name = "accion_realizada", nullable = false, length = 20)
    private AccionRealizada accionRealizada = AccionRealizada.PENDIENTE;

    @Column(name = "valor_medido", length = 250)
    private String valorMedido;

    @Column(name = "observacion_tecnica", columnDefinition = "TEXT")
    private String observacionTecnica;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
