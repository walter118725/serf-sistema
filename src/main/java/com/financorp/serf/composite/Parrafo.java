package com.financorp.serf.composite;

/**
 * COMPOSITE - Párrafo
 * 
 * Otro componente HOJA: solo texto, sin hijos
 */
public class Parrafo extends ComponenteReporte {
    
    private String texto;
    
    public Parrafo(String texto) {
        super("Párrafo");
        this.texto = texto;
    }
    
    @Override
    public void agregar(ComponenteReporte componente) {
        throw new UnsupportedOperationException(
            "❌ Un párrafo no puede tener hijos"
        );
    }
    
    @Override
    public void eliminar(ComponenteReporte componente) {
        throw new UnsupportedOperationException(
            "❌ Un párrafo no puede tener hijos"
        );
    }
    
    @Override
    public ComponenteReporte obtenerHijo(int index) {
        throw new UnsupportedOperationException(
            "❌ Un párrafo no tiene hijos"
        );
    }
    
    /**
     * Renderizar el párrafo con indentación
     */
    @Override
    public String renderizar() {
        return "    " + texto;
    }
    
    @Override
    public int obtenerNivel() {
        return 3;
    }
}

/*
 * ═══ EJEMPLO DE USO DEL PATRÓN COMPOSITE ═══
 * 
 * // Crear la estructura:
 * Seccion reporteAnual = new Seccion("Reporte Anual 2025");
 * 
 * Seccion trimestre1 = new Seccion("Primer Trimestre", 2);
 * Subseccion enero = new Subseccion("Enero", "Ventas: $50,000");
 * Subseccion febrero = new Subseccion("Febrero", "Ventas: $55,000");
 * 
 * trimestre1.agregar(enero);
 * trimestre1.agregar(febrero);
 * reporteAnual.agregar(trimestre1);
 * 
 * // Renderizar todo
 * String reporte = reporteAnual.renderizar();
 * 
 * // Resultado:
 * // = Reporte Anual 2025 =
 * //   == Primer Trimestre ==
 * //     • Enero: Ventas: $50,000
 * //     • Febrero: Ventas: $55,000
 */