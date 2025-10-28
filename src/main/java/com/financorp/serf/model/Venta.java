package com.financorp.serf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MODELO: Venta
 * 
 * Representa una venta realizada en alguna filial
 * Ejemplo: 5 laptops vendidas en Perú el 27/10/2025
 */
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String pais;

    @Column(nullable = false)
    private BigDecimal total;

    private String moneda;

    public Venta() {}

    public Venta(Long id, Producto producto, Integer cantidad, LocalDate fecha, String pais, BigDecimal total, String moneda) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.pais = pais;
        this.total = total;
        this.moneda = moneda;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Producto producto;
        private Integer cantidad;
        private LocalDate fecha;
        private String pais;
        private BigDecimal total;
        private String moneda;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder producto(Producto producto) { this.producto = producto; return this; }
        public Builder cantidad(Integer cantidad) { this.cantidad = cantidad; return this; }
        public Builder fecha(LocalDate fecha) { this.fecha = fecha; return this; }
        public Builder pais(String pais) { this.pais = pais; return this; }
        public Builder total(BigDecimal total) { this.total = total; return this; }
        public Builder moneda(String moneda) { this.moneda = moneda; return this; }

        public Venta build() { return new Venta(id, producto, cantidad, fecha, pais, total, moneda); }
    }
}