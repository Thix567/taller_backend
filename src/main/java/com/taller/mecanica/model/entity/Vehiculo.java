package com.taller.mecanica.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taller.mecanica.model.enums.TipoCombustible;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;

@Entity
@Table(name = "vehiculos", uniqueConstraints = @UniqueConstraint(name = "uk_vehiculos_placa", columnNames = "placa"))
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @NotBlank(message = "La placa es obligatoria")
    @Size(max = 10, message = "La placa no debe superar 10 caracteres")
    @Column(nullable = false, length = 10)
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 60, message = "La marca no debe superar 60 caracteres")
    @Column(nullable = false, length = 60)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Size(max = 80, message = "El modelo no debe superar 80 caracteres")
    @Column(nullable = false, length = 80)
    private String modelo;

    @NotNull(message = "El anio es obligatorio")
    @Min(value = 1900, message = "El anio debe ser mayor a 1900")
    @Max(value = 2100, message = "El anio debe ser menor a 2100")
    @Column(nullable = false)
    private Integer anio;

    @NotNull(message = "El tipo de combustible es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_combustible", nullable = false, length = 20)
    private TipoCombustible tipoCombustible = TipoCombustible.GASOLINA;

    @Size(max = 60, message = "El VIN del motor no debe superar 60 caracteres")
    @Column(name = "vin_motor", length = 60)
    private String vinMotor;

    @Size(max = 40, message = "El color no debe superar 40 caracteres")
    @Column(length = 40)
    private String color;

    @Min(value = 0, message = "El kilometraje actual no puede ser negativo")
    @Column(name = "kilometraje_actual", nullable = false)
    private Integer kilometrajeActual = 0;

    @PrePersist
    @PreUpdate
    void normalizarPlaca() {
        if (placa != null) {
            placa = placa.trim().toUpperCase(Locale.ROOT);
        }
    }
}
