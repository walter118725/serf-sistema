package com.financorp.serf.composite;

public abstract class ComponenteReporte {
    
    protected String nombre;
    
    public ComponenteReporte(String nombre) {
        this.nombre = nombre;
    }
    
    public abstract String renderizar();
    
    public abstract void agregar(ComponenteReporte componente);
    
    public abstract void remover(ComponenteReporte componente);
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
