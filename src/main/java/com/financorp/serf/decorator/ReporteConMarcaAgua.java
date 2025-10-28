package com.financorp.serf.decorator;

import com.financorp.serf.builder.Reporte;

/**
 * DECORATOR - Añade marca de agua al reporte
 * 
 * Agrega texto de "CONFIDENCIAL" en varias líneas
 */
public class ReporteConMarcaAgua extends DecoradorReporte {
    
    public ReporteConMarcaAgua(Reporte reporte) {
        super(reporte);
    }
    
    /**
     * Generar el reporte CON marca de agua
     */
    @Override
    public String generar() {
        String contenidoBase = reporteBase.generarContenido();
        return agregarMarcaAgua(contenidoBase);
    }
    
    /**
     * Agregar marca de agua cada 5 líneas
     */
    private String agregarMarcaAgua(String contenido) {
        String[] lineas = contenido.split("\n");
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < lineas.length; i++) {
            resultado.append(lineas[i]);
            
            // Cada 5 líneas, agregar marca de agua
            if (i % 5 == 0) {
                resultado.append("  [📄 CONFIDENCIAL - FINANCORP]");
            }
            
            resultado.append("\n");
        }
        
        return resultado.toString();
    }
}