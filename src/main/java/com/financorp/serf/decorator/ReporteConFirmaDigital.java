package com.financorp.serf.decorator;

import com.financorp.serf.builder.Reporte;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * DECORATOR - Añade firma digital al reporte
 * 
 * Este decorador agrega seguridad mediante una firma digital
 */
public class ReporteConFirmaDigital extends DecoradorReporte {
    
    public ReporteConFirmaDigital(Reporte reporte) {
        super(reporte);
    }
    
    /**
     * Generar el reporte CON firma digital
     */
    @Override
    public String generar() {
        // Primero, obtener el contenido original
        String contenidoBase = reporteBase.generarContenido();
        
        // Luego, agregar la firma al final
        return contenidoBase + "\n" + agregarFirma();
    }
    
    /**
     * Crear la firma digital
     */
    private String agregarFirma() {
        String firma = """
            
            ╔════════════════════════════════════════╗
            ║       🔐 FIRMA DIGITAL VALIDADA       ║
            ║  Hash: %s            ║
            ║  Fecha: %s                    ║
            ║  Autorizado por: Sistema SERF         ║
            ╚════════════════════════════════════════╝""".formatted(
                generarHash(),
                LocalDateTime.now().toString().substring(0, 19)
            );
        return firma;
    }
    
    /**
     * Generar un hash simulado (en producción usarías SHA-256)
     */
    private String generarHash() {
        String contenido = reporteBase.getTitulo() + reporteBase.getFecha();
        return Base64.getEncoder()
            .encodeToString(contenido.getBytes())
            .substring(0, 16);
    }
}

