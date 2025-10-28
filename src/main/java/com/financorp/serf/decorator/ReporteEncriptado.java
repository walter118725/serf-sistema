package com.financorp.serf.decorator;

import com.financorp.serf.builder.Reporte;
import java.util.Base64;

/**
 * DECORATOR - Encripta el contenido del reporte
 * 
 * Convierte el contenido a Base64 (encriptación simple)
 * En producción usarías AES o RSA
 */
public class ReporteEncriptado extends DecoradorReporte {
    
    public ReporteEncriptado(Reporte reporte) {
        super(reporte);
    }
    
    /**
     * Generar el reporte ENCRIPTADO
     */
    @Override
    public String generar() {
        String contenidoBase = reporteBase.generarContenido();
        return encriptar(contenidoBase);
    }
    
    /**
     * Encriptar el contenido (simulación con Base64)
     */
    private String encriptar(String contenido) {
        // Convertir a Base64
        String encriptado = Base64.getEncoder().encodeToString(contenido.getBytes());
        
        // Devolver con información de encriptación
        return """
            🔒 ═══ REPORTE ENCRIPTADO ═══ 🔒
            
            Algoritmo: Base64
            Longitud: %d caracteres
            
            Contenido encriptado:
            %s...
            
            ⚠️ Para desencriptar, use el sistema SERF""".formatted(
                encriptado.length(),
                encriptado.substring(0, Math.min(200, encriptado.length()))
            );
    }
}

/*
 * ═══ EJEMPLO DE USO DEL PATRÓN DECORATOR ═══
 * 
 * // Crear un reporte base
 * Reporte reporteBase = new ReporteBuilder()
 *     .setTitulo("Ventas Q4")
 *     .setPais("PERU")
 *     .build();
 * 
 * // Decorarlo con firma digital
 * DecoradorReporte conFirma = new ReporteConFirmaDigital(reporteBase);
 * String reporteConFirma = conFirma.generar();
 * 
 * // Decorarlo con marca de agua
 * DecoradorReporte conMarca = new ReporteConMarcaAgua(reporteBase);
 * String reporteConMarca = conMarca.generar();
 * 
 * // ¡Puedes combinar decoradores!
 * // (Aunque en este ejemplo simple no lo mostramos)
 * 
 * // El reporte original NO cambia
 * String reporteOriginal = reporteBase.generarContenido();
 * // reporteOriginal sigue sin firma ni marca de agua
 */