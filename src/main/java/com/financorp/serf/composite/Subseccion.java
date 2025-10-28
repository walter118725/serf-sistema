package com.financorp.serf.composite;

/**
 * COMPOSITE - Subsección
 * 
 * Es un componente HOJA: NO puede tener hijos
 * Es el final de una rama del árbol
 */
public class Subseccion extends ComponenteReporte {
    
    private String contenido;
    
    public Subseccion(String nombre, String contenido) {
        super(nombre);
        this.contenido = contenido;
    }
    
    /**
     * Las hojas NO pueden agregar hijos
     * Lanzamos excepción si lo intentan
     */
    @Override
    public void agregar(ComponenteReporte componente) {
        throw new UnsupportedOperationException(
            "❌ Una subsección no puede tener hijos"
        );
    }
    
    @Override
    public void eliminar(ComponenteReporte componente) {
        throw new UnsupportedOperationException(
            "❌ Una subsección no puede tener hijos"
        );
    }
    
    @Override
    public ComponenteReporte obtenerHijo(int index) {
        throw new UnsupportedOperationException(
            "❌ Una subsección no tiene hijos"
        );
    }
    
    /**
     * Renderizar la subsección
     */
    @Override
    public String renderizar() {
        return String.format("  • %s: %s", nombre, contenido);
    }
    
    @Override
    public int obtenerNivel() {
        return 2;
    }
}