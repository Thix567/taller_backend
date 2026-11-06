package com.taller.mecanica.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taller.mecanica.model.enums.CategoriaItem;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plantilla_items_paquete")
@Getter
@Setter
@NoArgsConstructor
public class PlantillaItemPaquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paquete_id", nullable = false)
    @JsonIgnore
    private PaqueteServicio paquete;

    @NotNull(message = "La categoria es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategoriaItem categoria;

    @NotBlank(message = "La descripcion de la tarea es obligatoria")
    @Size(max = 300, message = "La descripcion no debe superar 300 caracteres")
    @Column(name = "descripcion_tarea", nullable = false, length = 300)
    private String descripcionTarea;

    @Column(name = "es_condicional", nullable = false)
    private Boolean esCondicional = false;

    @Column(name = "requiere_ficha_tecnica", nullable = false)
    private Boolean requiereFichaTecnica = false;

    @NotNull(message = "El orden de visualizacion es obligatorio")
    @Min(value = 1, message = "El orden debe ser mayor o igual a 1")
    @Column(name = "orden_visualizacion", nullable = false)
    private Integer ordenVisualizacion;
}
