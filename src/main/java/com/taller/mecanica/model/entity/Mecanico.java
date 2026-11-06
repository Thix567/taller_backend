package com.taller.mecanica.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mecanicos", uniqueConstraints = @UniqueConstraint(name = "uk_mecanicos_dni", columnNames = "dni"))
@Getter
@Setter
@NoArgsConstructor
public class Mecanico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 20, message = "El DNI no debe superar 20 caracteres")
    @Column(nullable = false, length = 20)
    private String dni;

    @Size(max = 100, message = "La especialidad no debe superar 100 caracteres")
    @Column(length = 100)
    private String especialidad;

    @Size(max = 20, message = "El telefono no debe superar 20 caracteres")
    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private Boolean activo = true;
}
