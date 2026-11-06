package com.taller.mecanica.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(name = "uk_clientes_dni_ruc", columnNames = "dni_ruc"))
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El DNI o RUC es obligatorio")
    @Size(max = 20, message = "El DNI/RUC no debe superar 20 caracteres")
    @Column(name = "dni_ruc", nullable = false, length = 20)
    private String dniRuc;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotBlank(message = "El telefono de WhatsApp es obligatorio")
    @Size(max = 20, message = "El telefono no debe superar 20 caracteres")
    @Column(name = "telefono_whatsapp", nullable = false, length = 20)
    private String telefonoWhatsapp;

    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 120, message = "El correo no debe superar 120 caracteres")
    @Column(length = 120)
    private String correo;

    @Size(max = 200, message = "La direccion no debe superar 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @PrePersist
    void prePersistir() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}
