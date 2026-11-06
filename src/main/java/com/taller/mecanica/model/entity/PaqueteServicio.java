package com.taller.mecanica.model.entity;

import com.taller.mecanica.model.enums.TipoCombustible;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paquetes_servicio")
@Getter
@Setter
@NoArgsConstructor
public class PaqueteServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del paquete es obligatorio")
    @Size(max = 120, message = "El nombre no debe superar 120 caracteres")
    @Column(nullable = false, length = 120)
    private String nombre;

    @NotNull(message = "El tipo de combustible es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible", nullable = false, length = 20)
    private TipoCombustible tipoCombustible = TipoCombustible.GASOLINA;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio base no puede ser negativo")
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @NotNull(message = "El tiempo estimado es obligatorio")
    @Min(value = 1, message = "El tiempo estimado debe ser al menos 1 hora")
    @Column(name = "tiempo_estimado_horas", nullable = false)
    private Integer tiempoEstimadoHoras;

    @NotNull(message = "La garantia en kilometros es obligatoria")
    @Min(value = 0, message = "La garantia en kilometros no puede ser negativa")
    @Column(name = "garantia_km", nullable = false)
    private Integer garantiaKm;

    @NotNull(message = "La garantia en meses es obligatoria")
    @Min(value = 0, message = "La garantia en meses no puede ser negativa")
    @Column(name = "garantia_meses", nullable = false)
    private Integer garantiaMeses;

    @Column(nullable = false)
    private Boolean activo = true;

    @Valid
    @OneToMany(mappedBy = "paquete", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("ordenVisualizacion ASC")
    private List<PlantillaItemPaquete> items = new ArrayList<>();

    public void agregarItem(PlantillaItemPaquete item) {
        item.setPaquete(this);
        this.items.add(item);
    }
}
