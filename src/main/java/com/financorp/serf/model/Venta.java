package com.financorp.serf.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String numeroFactura;
    
    @Column(nullable = false)
    private LocalDateTime fechaVenta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false)
    private String monedaLocal;
    
    private String cliente;
    
    private String vendedorResponsable;
    
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;
    
    @Column(nullable = false)
    private String paisFilial;
    
    // Precio convertido a EUR (moneda corporativa)
    private BigDecimal precioUnitarioEUR;
    
    private BigDecimal totalVentaEUR;
    
    public enum MetodoPago {
        EFECTIVO,
        TARJETA,
        TRANSFERENCIA
    }
    
    @PrePersist
    public void calcularTotal() {
        if (precioUnitarioEUR != null && cantidad != null) {
            this.totalVentaEUR = precioUnitarioEUR.multiply(new BigDecimal(cantidad));
        }
    }
}