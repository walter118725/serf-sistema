package com.financorp.serf.builder;

import com.financorp.serf.composite.ComponenteReporte;
import java.time.LocalDate;

/**
 * ═══════ PATRÓN BUILDER ═══════
 * 
 * ¿Qué hace?
 * - Permite construir objetos complejos paso a paso
 * - Hace el código más legible y fácil de usar
 * 
 * ¿Por qué es útil?
 * - Un reporte tiene muchos campos (título, fecha, país, secciones, etc.)
 * - Sin Builder: new Reporte(titulo, fecha, pais, tipo, secciones, ...)
 * - Con Builder: new ReporteBuilder().setTitulo("X").setPais("Y").build()
 * 
 * Ejemplo de la vida real:
 * Como pedir una hamburguesa: "Quiero hamburguesa CON queso, CON tomate, SIN cebolla"
 * No tienes que decir todo de una vez, vas agregando ingredientes
 */
public class ReporteBuilder {
    
    // El reporte que estamos construyendo
    private Reporte reporte;
    
    /**
     * Constructor: Crea un reporte vacío
     */
    public ReporteBuilder() {
        this.reporte = new Reporte();
    }
    
    /**
     * Métodos para ir agregando cosas al reporte
     * Cada método devuelve "this" para poder encadenar llamadas
     */
    
    public ReporteBuilder setTitulo(String titulo) {
        reporte.setTitulo(titulo);
        return this;  // Devuelve el builder para encadenar
    }
    
    public ReporteBuilder setFecha(LocalDate fecha) {
        reporte.setFecha(fecha);
        return this;
    }
    
    public ReporteBuilder setPais(String pais) {
        reporte.setPais(pais);
        return this;
    }
    
    public ReporteBuilder setTipo(String tipo) {
        reporte.setTipo(tipo);
        return this;
    }
    
    public ReporteBuilder agregarSeccion(ComponenteReporte seccion) {
        reporte.getSecciones().add(seccion);
        return this;
    }
    
    public ReporteBuilder agregarGrafico(String grafico) {
        reporte.getGraficos().add(grafico);
        return this;
    }
    
    public ReporteBuilder agregarTabla(String tabla) {
        reporte.getTablas().add(tabla);
        return this;
    }
    
    public ReporteBuilder setFooter(String footer) {
        reporte.setFooter(footer);
        return this;
    }
    
    public ReporteBuilder setAutor(String autor) {
        reporte.setAutor(autor);
        return this;
    }
    
    /**
     * Método final: Construir y devolver el reporte
     * 
     * Incluye validaciones: El reporte DEBE tener título y país
     */
    public Reporte build() {
        // Validar campos obligatorios
        if (reporte.getTitulo() == null || reporte.getTitulo().isEmpty()) {
            throw new IllegalStateException("❌ El reporte debe tener un título");
        }
        if (reporte.getPais() == null) {
            throw new IllegalStateException("❌ El reporte debe especificar un país");
        }
        
        return reporte;
    }
    
    /**
     * Resetear el builder para crear un nuevo reporte
     */
    public ReporteBuilder reset() {
        this.reporte = new Reporte();
        return this;
    }
}

/*
 * ═══ EJEMPLO DE USO ═══
 * 
 * // Crear un reporte paso a paso
 * Reporte reporte = new ReporteBuilder()
 *     .setTitulo("Reporte de Ventas - Perú")
 *     .setFecha(LocalDate.now())
 *     .setPais("PERU")
 *     .setTipo("VENTAS")
 *     .agregarSeccion(seccionResumen)
 *     .agregarSeccion(seccionDetalle)
 *     .setFooter("Generado por SERF")
 *     .build();
 * 
 * // ¡Mucho más legible que un constructor gigante!
 */