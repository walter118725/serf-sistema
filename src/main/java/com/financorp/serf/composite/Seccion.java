package com.financorp.serf.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * COMPOSITE - Sección
 * 
 * Es un componente COMPUESTO: puede contener otros componentes
 * Puede tener subsecciones, párrafos, etc.
 */
public class Seccion extends ComponenteReporte {
    
    private List<ComponenteReporte> hijos;  // Los componentes que contiene
    private String contenido;
    private int nivel;  // 1, 2, 3... para la jerarquía
    
    /**
     * Constructor con nivel por defecto
     */
    public Seccion(String nombre) {
        super(nombre);
        this.hijos = new ArrayList<>();
        this.nivel = 1;
    }
    
    /**
     * Constructor con nivel personalizado
     */
    public Seccion(String nombre, int nivel) {
        super(nombre);
        this.hijos = new ArrayList<>();
        this.nivel = nivel;
    }
    
    /**
     * AGREGAR un componente hijo
     */
    @Override
    public void agregar(ComponenteReporte componente) {
        hijos.add(componente);
    }
    
    /**
     * ELIMINAR un componente hijo
     */
    @Override
    public void eliminar(ComponenteReporte componente) {
        hijos.remove(componente);
    }
    
    /**
     * OBTENER un hijo por índice
     */
    @Override
    public ComponenteReporte obtenerHijo(int index) {
        if (index >= 0 && index < hijos.size()) {
            return hijos.get(index);
        }
        return null;
    }
    
    /**
     * RENDERIZAR esta sección y todos sus hijos
     * Este método es RECURSIVO: se llama a sí mismo para los hijos
     */
    @Override
    public String renderizar() {
        StringBuilder sb = new StringBuilder();
        
        // Encabezado de la sección (con = según el nivel)
        String prefijo = "=".repeat(nivel);
        sb.append(prefijo).append(" ").append(nombre).append(" ").append(prefijo).append("\n");
        
        // Contenido propio (si tiene)
        if (contenido != null && !contenido.isEmpty()) {
            sb.append(contenido).append("\n");
        }
        
        // Renderizar todos los hijos
        for (ComponenteReporte hijo : hijos) {
            sb.append(hijo.renderizar()).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public int obtenerNivel() {
        return nivel;
    }
    
    /**
     * Establecer contenido de la sección
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}

