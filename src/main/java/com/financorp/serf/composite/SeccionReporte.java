package com.financorp.serf.composite;

import java.util.ArrayList;
import java.util.List;

public class SeccionReporte extends ComponenteReporte {
    
    private final List<ComponenteReporte> componentes;
    
    public SeccionReporte(String nombre) {
        super(nombre);
        this.componentes = new ArrayList<>();
    }
    
    @Override
    public String renderizar() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== ").append(nombre.toUpperCase()).append(" ===\n");
        
        for (ComponenteReporte componente : componentes) {
            resultado.append(componente.renderizar()).append("\n");
        }
        
        return resultado.toString();
    }
    
    @Override
    public void agregar(ComponenteReporte componente) {
        componentes.add(componente);
    }
    
    @Override
    public void remover(ComponenteReporte componente) {
        componentes.remove(componente);
    }
    
    public List<ComponenteReporte> getComponentes() {
        return new ArrayList<>(componentes);
    }
}
