package com.financorp.serf.decorator;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.model.reporte.Reporte;

/**
 * Factory para crear reportes con decoradores aplicados
 * Simplifica la aplicación de patrones de seguridad a reportes
 */
public class ReporteDecoratorFactory {
    
    private final ConfiguracionGlobal configuracion = ConfiguracionGlobal.getInstance();
    
    /**
     * Crea un reporte básico sin decoradores
     */
    public ReporteComponent crearReporteBasico(Reporte reporte) {
        return new ReporteBasico(reporte);
    }
    
    /**
     * Crea un reporte con marca de agua
     */
    public ReporteComponent crearReporteConMarcaAgua(Reporte reporte) {
        ReporteComponent reporteBasico = new ReporteBasico(reporte);
        return new MarcaAguaDecorator(reporteBasico);
    }
    
    /**
     * Crea un reporte con firma digital
     */
    public ReporteComponent crearReporteConFirmaDigital(Reporte reporte) {
        ReporteComponent reporteBasico = new ReporteBasico(reporte);
        String autoridadFirmante = configuracion.getFirmaDigitalAutorizada();
        return new FirmaDigitalDecorator(reporteBasico, autoridadFirmante);
    }
    
    /**
     * Crea un reporte con seguridad completa (marca de agua + firma digital)
     */
    public ReporteComponent crearReporteSeguro(Reporte reporte) {
        ReporteComponent reporteBasico = new ReporteBasico(reporte);
        
        // Aplicar marca de agua
        ReporteComponent conMarcaAgua = new MarcaAguaDecorator(reporteBasico);
        
        // Aplicar firma digital
        String autoridadFirmante = configuracion.getFirmaDigitalAutorizada();
        ReporteComponent conFirmaDigital = new FirmaDigitalDecorator(conMarcaAgua, autoridadFirmante);
        
        return conFirmaDigital;
    }
    
    /**
     * Crea un reporte con configuración personalizada
     */
    public ReporteComponent crearReportePersonalizado(Reporte reporte, 
                                                     boolean conMarcaAgua, 
                                                     boolean conFirmaDigital,
                                                     String textoMarcaAgua,
                                                     String autoridadFirmante) {
        ReporteComponent reporteComponent = new ReporteBasico(reporte);
        
        if (conMarcaAgua) {
            reporteComponent = new MarcaAguaDecorator(reporteComponent, textoMarcaAgua, "ENCABEZADO");
        }
        
        if (conFirmaDigital) {
            String autoridad = autoridadFirmante != null ? autoridadFirmante : configuracion.getFirmaDigitalAutorizada();
            reporteComponent = new FirmaDigitalDecorator(reporteComponent, autoridad);
        }
        
        return reporteComponent;
    }
}