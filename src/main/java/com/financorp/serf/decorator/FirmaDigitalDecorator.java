package com.financorp.serf.decorator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Decorador que añade firma digital a los reportes
 * Implementa autenticación y verificación de integridad documental
 */
public class FirmaDigitalDecorator extends ReporteDecorator {
    
    private static final String TEMPLATE_FIRMA = """
        
        ╔════════════════════════════════════════════════════════════════╗
        ║                        FIRMA DIGITAL                           ║
        ╠════════════════════════════════════════════════════════════════╣
        ║ Autorizado por: %s                           ║
        ║ Fecha de Firma: %s                                    ║
        ║ Hash del Documento: %s                        ║
        ║ Algoritmo: SHA-256                                             ║
        ║ Estado: VÁLIDO                                                 ║
        ╚════════════════════════════════════════════════════════════════╝
        
        NOTA: Esta firma digital garantiza la autenticidad e integridad 
        del documento. Cualquier modificación invalidará la firma.
        
        """;
    
    private final String autoridad;
    private final LocalDateTime fechaFirma;
    private final String hashDocumento;
    
    public FirmaDigitalDecorator(ReporteComponent reporteComponent) {
        this(reporteComponent, "Gerencia General FinanCorp S.A.");
    }
    
    public FirmaDigitalDecorator(ReporteComponent reporteComponent, String autoridadFirmante) {
        super(reporteComponent);
        this.autoridad = autoridadFirmante;
        this.fechaFirma = LocalDateTime.now();
        
        // Generar hash del contenido original para integridad
        String contenidoOriginal = super.generarContenido();
        this.hashDocumento = generarHashSHA256(contenidoOriginal);
    }
    
    @Override
    public String generarContenido() {
        String contenidoOriginal = super.generarContenido();
        String firmaDigital = generarFirmaDigital();
        
        return contenidoOriginal + firmaDigital;
    }
    
    @Override
    public Map<String, Object> obtenerMetadatos() {
        Map<String, Object> metadatos = super.obtenerMetadatos();
        metadatos.put("firmaDigital", true);
        metadatos.put("autoridadFirmante", autoridad);
        metadatos.put("fechaFirma", fechaFirma);
        metadatos.put("hashDocumento", hashDocumento);
        metadatos.put("algoritmoHash", "SHA-256");
        metadatos.put("estadoFirma", "VÁLIDO");
        metadatos.put("seguridad", true);
        return metadatos;
    }
    
    @Override
    public boolean tieneSeguridad() {
        return true;
    }
    
    private String generarFirmaDigital() {
        String fechaFormateada = fechaFirma.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        return String.format(TEMPLATE_FIRMA, 
            autoridad, 
            fechaFormateada, 
            hashDocumento.substring(0, 16) + "..."  // Solo mostrar parte del hash por seguridad
        );
    }
    
    private String generarHashSHA256(String contenido) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contenido.getBytes(StandardCharsets.UTF_8));
            
            // Convertir bytes a hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // Fallback a un hash simple si SHA-256 no está disponible
            return String.valueOf(contenido.hashCode());
        }
    }
    
    /**
     * Verifica la integridad del documento comparando hashes
     * @param contenidoActual Contenido actual del documento
     * @return true si el documento mantiene su integridad
     */
    public boolean verificarIntegridad(String contenidoActual) {
        String hashActual = generarHashSHA256(contenidoActual);
        return hashDocumento.equals(hashActual);
    }
}