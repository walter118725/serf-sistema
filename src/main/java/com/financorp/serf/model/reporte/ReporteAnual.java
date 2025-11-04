package com.financorp.serf.model.reporte;

public class ReporteAnual extends Reporte {
    
    private int anoReporte;
    
    public ReporteAnual() {
        super();
        this.tipoReporte = "ANUAL";
    }
    
    public ReporteAnual(int ano) {
        this();
        this.anoReporte = ano;
    }
    
    @Override
    public String generarContenido() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("=== REPORTE ANUAL ===\n");
        contenido.append("Empresa: ").append(empresa).append("\n");
        contenido.append("Año: ").append(anoReporte).append("\n");
        contenido.append("Fecha de generación: ").append(fechaGeneracion).append("\n");
        
        if (datos != null) {
            datos.forEach((key, value) -> 
                contenido.append(key).append(": ").append(value).append("\n")
            );
        }
        
        contenido.append("\nFirma autorizada: ").append(firmaAutorizada).append("\n");
        return contenido.toString();
    }
    
    public int getAnoReporte() {
        return anoReporte;
    }
    
    public void setAnoReporte(int anoReporte) {
        this.anoReporte = anoReporte;
    }
}
