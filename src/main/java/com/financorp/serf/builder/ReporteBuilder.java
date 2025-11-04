package com.financorp.serf.builder;

import com.financorp.serf.model.reporte.*;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Map;

@Component
public class ReporteBuilder {
    
    private Reporte reporte;
    
    public ReporteBuilder crearReporteMensual() {
        this.reporte = new ReporteMensual();
        return this;
    }
    
    public ReporteBuilder crearReporteAnual() {
        this.reporte = new ReporteAnual();
        return this;
    }
    
    public ReporteBuilder crearReporteTrimestral() {
        this.reporte = new ReporteTrimestral();
        return this;
    }
    
    public ReporteBuilder conEmpresa(String empresa) {
        if (reporte != null) {
            reporte.setEmpresa(empresa);
        }
        return this;
    }
    
    public ReporteBuilder conFirmaAutorizada(String firma) {
        if (reporte != null) {
            reporte.setFirmaAutorizada(firma);
        }
        return this;
    }
    
    public ReporteBuilder conFechaGeneracion(LocalDate fecha) {
        if (reporte != null) {
            reporte.setFechaGeneracion(fecha);
        }
        return this;
    }
    
    public ReporteBuilder conDatos(Map<String, Object> datos) {
        if (reporte != null) {
            reporte.setDatos(datos);
        }
        return this;
    }
    
    public ReporteBuilder conTipoReporte(String tipo) {
        if (reporte != null) {
            reporte.setTipoReporte(tipo);
        }
        return this;
    }
    
    public Reporte construir() {
        if (reporte == null) {
            throw new IllegalStateException("Debe crear un reporte antes de construirlo");
        }
        
        // Configurar valores por defecto si no se han establecido
        if (reporte.getFechaGeneracion() == null) {
            reporte.setFechaGeneracion(LocalDate.now());
        }
        
        if (reporte.getEmpresa() == null) {
            reporte.setEmpresa("FinanCorp S.A.");
        }
        
        if (reporte.getFirmaAutorizada() == null) {
            reporte.setFirmaAutorizada("Gerencia General");
        }
        
        Reporte reporteCompleto = reporte;
        this.reporte = null; // Limpiar para siguiente uso
        return reporteCompleto;
    }
}
