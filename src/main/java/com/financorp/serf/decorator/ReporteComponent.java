package com.financorp.serf.decorator;

/**
 * Componente base para el patrón Decorator
 * Define la interfaz para reportes que pueden ser decorados
 */
public interface ReporteComponent {
    
    /**
     * Genera el contenido del reporte
     * @return Contenido del reporte como String
     */
    String generarContenido();
    
    /**
     * Obtiene los metadatos del reporte
     * @return Map con metadatos
     */
    java.util.Map<String, Object> obtenerMetadatos();
    
    /**
     * Obtiene el tipo de reporte
     * @return Tipo de reporte
     */
    String getTipoReporte();
    
    /**
     * Verifica si el reporte tiene seguridad aplicada
     * @return true si tiene seguridad, false en caso contrario
     */
    boolean tieneSeguridad();
}