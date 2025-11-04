package com.financorp.serf.model.reporte;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class ReporteMensual extends Reporte {
    
    private LocalDate mesReporte;
    
    public ReporteMensual() {
        super();
        this.tipoReporte = "MENSUAL";
    }
    
    public ReporteMensual(LocalDate mes) {
        this();
        this.mesReporte = mes;
    }
    
    @Override
    public String generarContenido() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("=== REPORTE MENSUAL ===\n");
        contenido.append("Empresa: ").append(empresa).append("\n");
        contenido.append("Mes: ").append(mesReporte.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        contenido.append(" ").append(mesReporte.getYear()).append("\n");
        contenido.append("Fecha de generación: ").append(fechaGeneracion).append("\n");
        
        if (datos != null) {
            datos.forEach((key, value) -> 
                contenido.append(key).append(": ").append(value).append("\n")
            );
        }
        
        contenido.append("\nFirma autorizada: ").append(firmaAutorizada).append("\n");
        return contenido.toString();
    }
    
    public LocalDate getMesReporte() {
        return mesReporte;
    }
    
    public void setMesReporte(LocalDate mesReporte) {
        this.mesReporte = mesReporte;
    }
}
