package com.financorp.serf.prototype;

import java.util.HashMap;
import java.util.Map;

/**
 * PATRÓN PROTOTYPE - Plantilla de Reportes de Ventas
 * 
 * Esta plantilla se puede clonar y adaptar para diferentes países
 */
public class PlantillaVentas implements PlantillaReporte {

    private String estructura;
    private String formato;
    private Map<String, String> estilos;  // Colores, fuentes, etc.
    private String idioma;
    private String moneda;
    
    /**
     * Constructor: Crea la plantilla base
     */
    public PlantillaVentas() {
        this.estructura = "ENCABEZADO|RESUMEN_VENTAS|DETALLE_PRODUCTOS|GRAFICOS|FOOTER";
        this.formato = "PDF";
        this.estilos = new HashMap<>();
        this.estilos.put("color_principal", "#2563EB");  // Azul
        this.estilos.put("fuente", "Arial");
        this.estilos.put("tamano_fuente", "12");
        this.idioma = "es";
        this.moneda = "USD";
    }
    
    /**
     * CLONAR: Crear una copia de esta plantilla
     * 
     * IMPORTANTE: Hacemos una "copia profunda" del Map de estilos
     * Si no hacemos esto, el clon y el original compartirían el mismo Map
     */
    @Override
    public PlantillaReporte clonar() {
        PlantillaVentas clon = new PlantillaVentas();
        clon.setEstructura(this.estructura);
        clon.setFormato(this.formato);
        // Crear un nuevo HashMap con los mismos valores
        clon.setEstilos(new HashMap<>(this.estilos));
        clon.setIdioma(this.idioma);
        clon.setMoneda(this.moneda);
        return clon;
    }
    
    /**
     * CONFIGURAR por país
     * Cambia moneda, idioma y colores según el país
     */
    @Override
    public void configurarPais(String pais) {
        switch (pais.toUpperCase()) {
            case "PERU":
                this.moneda = "PEN";  // Soles
                this.idioma = "es";
                this.estilos.put("color_principal", "#DC2626");  // Rojo
                break;
                
            case "MEXICO":
                this.moneda = "MXN";  // Pesos mexicanos
                this.idioma = "es";
                this.estilos.put("color_principal", "#16A34A");  // Verde
                break;
                
            case "ESPAÑA":
                this.moneda = "EUR";  // Euros
                this.idioma = "es";
                this.estilos.put("color_principal", "#EAB308");  // Amarillo
                break;
                
            default:
                this.moneda = "USD";
                this.idioma = "en";
        }
    }
    
    @Override
    public String generarEstructura() {
        return String.format("Plantilla Ventas [%s] - Moneda: %s - Idioma: %s", 
                           formato, moneda, idioma);
    }
    
    @Override
    public String getFormato() {
        return formato;
    }

    // Getters / Setters
    public String getEstructura() { return estructura; }
    public void setEstructura(String estructura) { this.estructura = estructura; }

    public void setFormato(String formato) { this.formato = formato; }

    public Map<String, String> getEstilos() { return estilos; }
    public void setEstilos(Map<String, String> estilos) { this.estilos = estilos; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}
