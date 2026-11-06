package com.taller.mecanica.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taller.mecanica.model.enums.TipoAdicional;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "adicionales_orden")
@Getter
@Setter
@NoArgsConstructor
public class AdicionalOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "informe_id", nullable = false)
    @JsonIgnore
    private InformeVehiculo informe;

    @Column(nullable = false, length = 300)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAdicional tipo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "aprobado_por_cliente", nullable = false)
    private Boolean aprobadoPorCliente = false;

    @Column(name = "cobra_arandela", nullable = false)
    private Boolean cobraArandela = true;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public boolean seCobra() {
        return Boolean.TRUE.equals(aprobadoPorCliente) && Boolean.TRUE.equals(cobraArandela);
    }
}
