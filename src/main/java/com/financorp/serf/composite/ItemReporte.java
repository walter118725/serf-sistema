package com.financorp.serf.composite;

public class ItemReporte extends ComponenteReporte {
    
    private String contenido;
    
    public ItemReporte(String nombre, String contenido) {
        super(nombre);
        this.contenido = contenido;
    }
    
    @Override
    public String renderizar() {
        return nombre + ": " + contenido;
    }
    
    @Override
    public void agregar(ComponenteReporte componente) {
        throw new UnsupportedOperationException("No se pueden agregar componentes a un item");
    }
    
    @Override
    public void remover(ComponenteReporte componente) {
        throw new UnsupportedOperationException("No se pueden remover componentes de un item");
    }
    
    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
