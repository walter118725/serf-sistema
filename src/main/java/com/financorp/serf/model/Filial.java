package com.financorp.serf.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * MODELO: Filial
 * 
 * Representa una sede/oficina de FinanCorp en un país
 * Ejemplo: FinanCorp Perú en Lima, FinanCorp México en CDMX
 */
@Entity
@Table(name = "filiales")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;  // "FinanCorp Perú", "FinanCorp México"

    @Column(nullable = false, unique = true)  // unique = No puede repetirse
    private String pais;  // PERU, MEXICO, ESPAÑA

    @Column(nullable = false)
    private String moneda;  // PEN, MXN, EUR

    private String direccion;

    private String telefono;

    @Column(name = "gerente_nombre")
    private String gerenteNombre;  // Nombre del gerente de la filial

    public Filial() {
    }

    public Filial(Long id, String nombre, String pais, String moneda, String direccion, String telefono, String gerenteNombre) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.moneda = moneda;
        this.direccion = direccion;
        this.telefono = telefono;
        this.gerenteNombre = gerenteNombre;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getGerenteNombre() { return gerenteNombre; }
    public void setGerenteNombre(String gerenteNombre) { this.gerenteNombre = gerenteNombre; }

    // Simple builder to replace Lombok.builder()
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String nombre;
        private String pais;
        private String moneda;
        private String direccion;
        private String telefono;
        private String gerenteNombre;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder nombre(String nombre) { this.nombre = nombre; return this; }
        public Builder pais(String pais) { this.pais = pais; return this; }
        public Builder moneda(String moneda) { this.moneda = moneda; return this; }
        public Builder direccion(String direccion) { this.direccion = direccion; return this; }
        public Builder telefono(String telefono) { this.telefono = telefono; return this; }
        public Builder gerenteNombre(String gerenteNombre) { this.gerenteNombre = gerenteNombre; return this; }

        public Filial build() {
            return new Filial(id, nombre, pais, moneda, direccion, telefono, gerenteNombre);
        }
    }
}