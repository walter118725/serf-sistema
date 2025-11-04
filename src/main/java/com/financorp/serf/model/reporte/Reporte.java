package com.financorp.serf.model.reporte;

import java.time.LocalDate;
import java.util.Map;

public abstract class Reporte implements Cloneable {
    protected String tipoReporte;
    protected LocalDate fechaGeneracion;
    protected String empresa;
    protected String firmaAutorizada;
    protected Map<String, Object> datos;
    
    public Reporte() {
        this.fechaGeneracion = LocalDate.now();
    }
    
    public abstract String generarContenido();
    
    @Override
    public Reporte clone() throws CloneNotSupportedException {
        return (Reporte) super.clone();
    }
    
    // Getters y Setters
    public String getTipoReporte() {
        return tipoReporte;
    }
    
    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }
    
    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }
    
    public void setFechaGeneracion(LocalDate fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }
    
    public String getEmpresa() {
        return empresa;
    }
    
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
    
    public String getFirmaAutorizada() {
        return firmaAutorizada;
    }
    
    public void setFirmaAutorizada(String firmaAutorizada) {
        this.firmaAutorizada = firmaAutorizada;
    }
    
    public Map<String, Object> getDatos() {
        return datos;
    }
    
    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }
}
