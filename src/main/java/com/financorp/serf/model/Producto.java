package com.financorp.serf.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(length = 1000)
    private String descripcionTecnica;
    
    @Enumerated(EnumType.STRING)
    private CategoriaProducto categoria;
    
    @Column(nullable = false)
    private BigDecimal costoImportacion;
    
    @Column(nullable = false)
    private String monedaOrigen; // CNY, USD, etc.
    
    @Column(nullable = false)
    private BigDecimal precioVentaSugerido;
    
    private String proveedor;
    
    private LocalDate fechaImportacion;
    
    @Column(nullable = false)
    private Integer stockInicial;
    
    @Column(nullable = false)
    private Integer stockActual;
    
    public enum CategoriaProducto {
        LAPTOP,
        SMARTPHONE,
        ACCESORIO,
        EQUIPO_RED,
        OTRO
    }
}