package com.financorp.serf.decorator;

import java.util.Map;

/**
 * Decorador base abstracto que implementa el patrón Decorator
 * Define la estructura común para todos los decoradores de reportes
 */
public abstract class ReporteDecorator implements ReporteComponent {
    
    protected ReporteComponent reporteComponent;
    
    public ReporteDecorator(ReporteComponent reporteComponent) {
        this.reporteComponent = reporteComponent;
    }
    
    @Override
    public String generarContenido() {
        return reporteComponent.generarContenido();
    }
    
    @Override
    public Map<String, Object> obtenerMetadatos() {
        return reporteComponent.obtenerMetadatos();
    }
    
    @Override
    public String getTipoReporte() {
        return reporteComponent.getTipoReporte();
    }
    
    @Override
    public boolean tieneSeguridad() {
        return reporteComponent.tieneSeguridad();
    }
}