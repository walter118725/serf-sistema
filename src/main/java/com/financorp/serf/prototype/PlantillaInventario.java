package com.financorp.serf.prototype;

import java.util.HashMap;
import java.util.Map;

/**
 * PATRÓN PROTOTYPE - Plantilla de Reportes de Inventario
 */
public class PlantillaInventario implements PlantillaReporte {

    private String estructura;
    private String formato;
    private Map<String, String> estilos;
    private String idioma;
    private boolean incluirAlertas;  // ¿Mostrar alertas de stock bajo?
    
    /**
     * Constructor: Plantilla base de inventario
     */
    public PlantillaInventario() {
        this.estructura = "ENCABEZADO|RESUMEN_STOCK|PRODUCTOS_BAJO_STOCK|DISTRIBUCION|FOOTER";
        this.formato = "EXCEL";
        this.estilos = new HashMap<>();
        this.estilos.put("color_alerta", "#DC2626");  // Rojo para alertas
        this.estilos.put("color_ok", "#16A34A");  // Verde para OK
        this.idioma = "es";
        this.incluirAlertas = true;
    }
    
    /**
     * CLONAR esta plantilla
     */
    @Override
    public PlantillaReporte clonar() {
        PlantillaInventario clon = new PlantillaInventario();
        clon.setEstructura(this.estructura);
        clon.setFormato(this.formato);
        clon.setEstilos(new HashMap<>(this.estilos));
        clon.setIdioma(this.idioma);
        clon.setIncluirAlertas(this.incluirAlertas);
        return clon;
    }
    
    /**
     * Configurar por país
     */
    @Override
    public void configurarPais(String pais) {
        // Asignar idioma según el país
        switch (pais.toUpperCase()) {
            case "ESPAÑA":
                this.idioma = "es";
                break;
            case "PORTUGAL":
                this.idioma = "pt";
                break;
            case "BRASIL":
                this.idioma = "pt-BR";
                break;
            default:
                this.idioma = "es";
        }
    }
    
    @Override
    public String generarEstructura() {
        return String.format("Plantilla Inventario [%s] - Alertas: %s", 
                           formato, incluirAlertas ? "Activas" : "Desactivadas");
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

    public boolean isIncluirAlertas() { return incluirAlertas; }
    public void setIncluirAlertas(boolean incluirAlertas) { this.incluirAlertas = incluirAlertas; }
}

/*
 * ═══ EJEMPLO DE USO DEL PATRÓN PROTOTYPE ═══
 * 
 * // Crear plantilla base
 * PlantillaVentas plantillaBase = new PlantillaVentas();
 * 
 * // Clonar para Perú
 * PlantillaReporte plantillaPeru = plantillaBase.clonar();
 * plantillaPeru.configurarPais("PERU");
 * // Ahora tiene: moneda PEN, color rojo
 * 
 * // Clonar para México
 * PlantillaReporte plantillaMexico = plantillaBase.clonar();
 * plantillaMexico.configurarPais("MEXICO");
 * // Ahora tiene: moneda MXN, color verde
 * 
 * // ¡Los clones son independientes!
 * // Cambiar uno NO afecta al otro
 */