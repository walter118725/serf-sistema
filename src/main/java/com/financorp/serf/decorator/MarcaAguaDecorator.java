package com.financorp.serf.decorator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Decorador que añade marca de agua a los reportes
 * Implementa funcionalidad de seguridad documental
 */
public class MarcaAguaDecorator extends ReporteDecorator {
    
    private static final String MARCA_AGUA_DEFAULT = """
        
        =====================================
        |      FINANCORP S.A. - CONFIDENCIAL      |
        |         Documento Corporativo           |
        |    Generado: %s    |
        =====================================
        
        """;
    
    private final String textoMarcaAgua;
    private final String posicion;
    
    public MarcaAguaDecorator(ReporteComponent reporteComponent) {
        this(reporteComponent, null, "ENCABEZADO");
    }
    
    public MarcaAguaDecorator(ReporteComponent reporteComponent, String textoPersonalizado, String posicion) {
        super(reporteComponent);
        this.textoMarcaAgua = textoPersonalizado != null ? textoPersonalizado : MARCA_AGUA_DEFAULT;
        this.posicion = posicion != null ? posicion : "ENCABEZADO";
    }
    
    @Override
    public String generarContenido() {
        String contenidoOriginal = super.generarContenido();
        String marcaAgua = generarMarcaAgua();
        
        return switch (posicion.toUpperCase()) {
            case "ENCABEZADO" -> marcaAgua + contenidoOriginal;
            case "PIE" -> contenidoOriginal + marcaAgua;
            case "AMBOS" -> marcaAgua + contenidoOriginal + marcaAgua;
            default -> marcaAgua + contenidoOriginal;
        };
    }
    
    @Override
    public Map<String, Object> obtenerMetadatos() {
        Map<String, Object> metadatos = super.obtenerMetadatos();
        metadatos.put("marcaAgua", true);
        metadatos.put("posicionMarcaAgua", posicion);
        metadatos.put("fechaAplicacionMarcaAgua", LocalDateTime.now());
        metadatos.put("seguridad", true);
        return metadatos;
    }
    
    @Override
    public boolean tieneSeguridad() {
        return true;
    }
    
    private String generarMarcaAgua() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return String.format(textoMarcaAgua, timestamp);
    }
}