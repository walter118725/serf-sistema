package com.financorp.serf.composite;

/**
 * ═══════ PATRÓN COMPOSITE ═══════
 * 
 * ¿Qué hace?
 * - Permite crear estructuras tipo árbol (jerarquías)
 * - Un reporte puede tener secciones
 * - Cada sección puede tener subsecciones
 * - Cada subsección puede tener párrafos
 * 
 * ¿Por qué es útil?
 * - Puedes tratar elementos individuales y grupos de la misma manera
 * - Anidamiento ilimitado: puedes tener secciones dentro de secciones
 * 
 * Ejemplo de la vida real:
 * Un libro tiene:
 * - Capítulos (que pueden tener)
 *   - Secciones (que pueden tener)
 *     - Subsecciones (que pueden tener)
 *       - Párrafos
 */
public abstract class ComponenteReporte {
    
    protected String nombre;
    
    public ComponenteReporte(String nombre) {
        this.nombre = nombre;
    }
    
    // Métodos que deben implementar las subclases
    public abstract void agregar(ComponenteReporte componente);
    public abstract void eliminar(ComponenteReporte componente);
    public abstract ComponenteReporte obtenerHijo(int index);
    public abstract String renderizar();
    public abstract int obtenerNivel();
}

