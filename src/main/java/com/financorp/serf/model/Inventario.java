package com.financorp.serf.model;

import jakarta.persistence.*;

/**
 * MODELO: Inventario
 * 
 * Representa cuántos productos hay en stock en cada filial
 * Ejemplo: 25 laptops Dell en el almacén de Lima, Perú
 */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private String ubicacion;

    @Column(nullable = false)
    private String pais;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    public Inventario() {}

    public Inventario(Long id, Producto producto, Integer cantidad, String ubicacion, String pais, Integer stockMinimo) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
        this.pais = pais;
        this.stockMinimo = stockMinimo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Producto producto;
        private Integer cantidad;
        private String ubicacion;
        private String pais;
        private Integer stockMinimo;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder producto(Producto producto) { this.producto = producto; return this; }
        public Builder cantidad(Integer cantidad) { this.cantidad = cantidad; return this; }
        public Builder ubicacion(String ubicacion) { this.ubicacion = ubicacion; return this; }
        public Builder pais(String pais) { this.pais = pais; return this; }
        public Builder stockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; return this; }

        public Inventario build() { return new Inventario(id, producto, cantidad, ubicacion, pais, stockMinimo); }
    }
}