package com.taller.mecanica.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taller.mecanica.model.enums.EtapaEvidencia;
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
@Table(name = "evidencias_fotograficas")
@Getter
@Setter
@NoArgsConstructor
public class EvidenciaFotografica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informe_id", nullable = false)
    @JsonIgnore
    private InformeVehiculo informe;

    @Column(name = "url_imagen", nullable = false, length = 500)
    private String urlImagen;

    @Column(name = "descripcion_foto", length = 300)
    private String descripcionFoto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EtapaEvidencia etapa = EtapaEvidencia.INGRESO;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida = LocalDateTime.now();
}
