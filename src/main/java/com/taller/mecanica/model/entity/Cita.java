package com.taller.mecanica.model.entity;

import com.taller.mecanica.model.enums.EstadoCita;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El vehiculo es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @NotNull(message = "El paquete deseado es obligatorio")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "paquete_id", nullable = false)
    private PaqueteServicio paqueteDeseado;

    @NotNull(message = "La fecha y hora de inicio es obligatoria")
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(name = "fecha_hora_fin_calculada", nullable = false)
    private LocalDateTime fechaHoraFinCalculada;

    @NotNull(message = "El estado de la cita es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cita", nullable = false, length = 20)
    private EstadoCita estadoCita = EstadoCita.PENDIENTE;

    @Size(max = 1000, message = "Las observaciones no deben superar 1000 caracteres")
    @Column(length = 1000)
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "informe_id")
    private Long informeId;
}
