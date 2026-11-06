package com.taller.mecanica.model.entity;

import com.taller.mecanica.model.enums.CategoriaInsumo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "insumos", uniqueConstraints = @UniqueConstraint(name = "uk_insumos_codigo", columnNames = "codigo"))
@Getter
@Setter
@NoArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    @Size(max = 30, message = "El codigo no debe superar 30 caracteres")
    @Column(nullable = false, length = 30)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotNull(message = "La categoria es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategoriaInsumo categoria;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @NotNull(message = "El stock minimo es obligatorio")
    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio de venta no puede ser negativo")
    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Size(max = 500, message = "La URL de la ficha tecnica no debe superar 500 caracteres")
    @Column(name = "url_ficha_tecnica_pdf", length = 500)
    private String urlFichaTecnicaPdf;
}
