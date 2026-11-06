package com.taller.mecanica.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;

@Entity
@Table(name = "consumos_insumo")
@Getter
@Setter
@NoArgsConstructor
public class ConsumoInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informe_id", nullable = false)
    @JsonIgnore
    private InformeVehiculo informe;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "url_ficha_tecnica_pdf", length = 500)
    private String urlFichaTecnicaPdf;
}
