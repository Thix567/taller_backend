package com.taller.mecanica.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "configuracion_taller")
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionTaller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del taller es obligatorio")
    @Size(max = 150)
    @Column(name = "nombre_taller", nullable = false, length = 150)
    private String nombreTaller;

    @Size(max = 20)
    @Column(name = "ruc", length = 20)
    private String ruc;

    @Size(max = 250)
    @Column(name = "direccion", length = 250)
    private String direccion;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 20)
    @Column(name = "telefono_whatsapp", length = 20)
    private String telefonoWhatsapp;

    @Size(max = 120)
    @Column(name = "correo", length = 120)
    private String correo;

    @Size(max = 500)
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @NotNull(message = "La capacidad maxima es obligatoria")
    @Min(value = 1, message = "La capacidad maxima debe ser al menos 1")
    @Column(name = "capacidad_maxima_simultanea", nullable = false)
    private Integer capacidadMaximaSimultanea = 3;

    @Size(max = 200)
    @Column(name = "horario_atencion", length = 200)
    private String horarioAtencion;

    @Column(name = "texto_garantia", columnDefinition = "TEXT")
    private String textoGarantia;

    @Column(name = "mensaje_recordatorio", columnDefinition = "TEXT")
    private String mensajeRecordatorio;
}
