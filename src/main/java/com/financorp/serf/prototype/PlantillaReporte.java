package com.financorp.serf.prototype;

/**
 * ═══════ PATRÓN PROTOTYPE - Interface ═══════
 * 
 * ¿Qué hace?
 * - Permite clonar (copiar) objetos existentes
 * - En vez de crear desde cero, copiamos uno que ya existe
 * 
 * ¿Por qué es útil?
 * - Crear un objeto puede ser costoso
 * - Si ya tengo uno parecido, mejor lo copio y lo modifico
 * 
 * Ejemplo de la vida real:
 * - Tienes un documento Word de "Reporte de Ventas México"
 * - En vez de crear uno nuevo desde cero para Perú
 * - Copias el de México y cambias "México" por "Perú"
 */
public interface PlantillaReporte {
    
    /**
     * Clonar la plantilla (crear una copia)
     */
    PlantillaReporte clonar();
    
    /**
     * Configurar la plantilla para un país específico
     * Cambia moneda, idioma, colores, etc.
     */
    void configurarPais(String pais);
    
    /**
     * Generar la estructura de la plantilla
     */
    String generarEstructura();
    
    /**
     * Obtener el formato de la plantilla
     */
    String getFormato();
}