package com.financorp.serf.decorator;

import com.financorp.serf.builder.Reporte;

/**
 * ═══════ PATRÓN DECORATOR ═══════
 * 
 * ¿Qué hace?
 * - Añade funcionalidades extra a un objeto SIN modificar su clase
 * - "Decora" o "envuelve" el objeto original
 * 
 * ¿Por qué es útil?
 * - Puedes agregar características de forma dinámica
 * - No necesitas modificar la clase original
 * - Puedes combinar múltiples decoradores
 * 
 * Ejemplo de la vida real:
 * Tienes un café simple. Puedes decorarlo con:
 * - Leche → Café con leche
 * - Crema → Café con leche y crema
 * - Chocolate → Café con leche, crema y chocolate
 * 
 * El café original no cambia, solo le agregas cosas encima
 */
public abstract class DecoradorReporte {
    
    // El reporte que vamos a "decorar"
    protected Reporte reporteBase;
    
    /**
     * Constructor: Recibe el reporte a decorar
     */
    public DecoradorReporte(Reporte reporte) {
        this.reporteBase = reporte;
    }
    
    /**
     * Método abstracto que implementarán los decoradores concretos
     * Cada decorador añade algo diferente
     */
    public abstract String generar();
}
