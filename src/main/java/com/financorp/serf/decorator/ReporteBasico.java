package com.financorp.serf.decorator;

import java.util.HashMap;
import java.util.Map;

import com.financorp.serf.model.reporte.Reporte;

/**
 * Implementación concreta del componente base
 * Envuelve un reporte existente para permitir decoración
 */
public class ReporteBasico implements ReporteComponent {
    
    private final Reporte reporte;
    private final Map<String, Object> metadatos;
    
    public ReporteBasico(Reporte reporte) {
        this.reporte = reporte;
        this.metadatos = new HashMap<>();
        inicializarMetadatos();
    }
    
    private void inicializarMetadatos() {
        metadatos.put("fechaGeneracion", reporte.getFechaGeneracion());
        metadatos.put("empresa", reporte.getEmpresa());
        metadatos.put("firmaAutorizada", reporte.getFirmaAutorizada());
        metadatos.put("tipoReporte", reporte.getTipoReporte());
        metadatos.put("seguridad", false);
        metadatos.put("marcaAgua", false);
        metadatos.put("firmaDigital", false);
    }
    
    @Override
    public String generarContenido() {
        return reporte.generarContenido();
    }
    
    @Override
    public Map<String, Object> obtenerMetadatos() {
        return new HashMap<>(metadatos);
    }
    
    @Override
    public String getTipoReporte() {
        return reporte.getTipoReporte();
    }
    
    @Override
    public boolean tieneSeguridad() {
        return (Boolean) metadatos.getOrDefault("seguridad", false);
    }
    
    // Getter para acceder al reporte interno
    public Reporte getReporte() {
        return reporte;
    }
    
    // Método para actualizar metadatos (usado por decoradores)
    protected void actualizarMetadato(String clave, Object valor) {
        metadatos.put(clave, valor);
    }
}