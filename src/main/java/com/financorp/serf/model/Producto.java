package com.financorp.serf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MODELO: Producto
 * 
 * Representa un producto importado que vende FinanCorp
 * Ejemplo: Laptop Dell Inspiron 15, Smartphone Xiaomi, etc.
 * 
 * @Entity = Esta clase es una tabla en la base de datos
 * @Data = Lombok genera automáticamente: getters, setters, toString, equals, hashCode
 * @Builder = Permite crear objetos fácilmente: Producto.builder().nombre("Laptop").build()
 */
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private BigDecimal precio;

    private String fabricante;

    @Column(name = "pais_origen")
    private String paisOrigen;

    private String descripcion;

    @Column(name = "fecha_importacion")
    private LocalDate fechaImportacion;

    public Producto() {}

    public Producto(Long id, String nombre, String categoria, BigDecimal precio, String fabricante, String paisOrigen, String descripcion, LocalDate fechaImportacion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.fabricante = fabricante;
        this.paisOrigen = paisOrigen;
        this.descripcion = descripcion;
        this.fechaImportacion = fechaImportacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }

    public String getPaisOrigen() { return paisOrigen; }
    public void setPaisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaImportacion() { return fechaImportacion; }
    public void setFechaImportacion(LocalDate fechaImportacion) { this.fechaImportacion = fechaImportacion; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String categoria;
        private BigDecimal precio;
        private String fabricante;
        private String paisOrigen;
        private String descripcion;
        private LocalDate fechaImportacion;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder categoria(String categoria) { this.categoria = categoria; return this; }
        public Builder precio(BigDecimal precio) { this.precio = precio; return this; }
        public Builder fabricante(String fabricante) { this.fabricante = fabricante; return this; }
        public Builder paisOrigen(String paisOrigen) { this.paisOrigen = paisOrigen; return this; }
        public Builder descripcion(String descripcion) { this.descripcion = descripcion; return this; }
        public Builder fechaImportacion(LocalDate fechaImportacion) { this.fechaImportacion = fechaImportacion; return this; }

        public Producto build() {
            return new Producto(id, nombre, categoria, precio, fabricante, paisOrigen, descripcion, fechaImportacion);
        }
    }
}