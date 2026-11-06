package com.taller.mecanica.model.entity;

import com.taller.mecanica.model.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_username", columnNames = "username"))
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 60, message = "El usuario no debe superar 60 caracteres")
    @Column(nullable = false, length = 60)
    private String username;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(max = 100, message = "El hash de contrasena no debe superar 100 caracteres")
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar 150 caracteres")
    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.ADMIN;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @PrePersist
    void prePersistir() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (rol == null) {
            rol = Rol.ADMIN;
        }
    }
}
