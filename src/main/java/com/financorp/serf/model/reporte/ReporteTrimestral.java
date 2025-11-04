package com.financorp.serf.model.reporte;

public class ReporteTrimestral extends Reporte {
    
    private int trimestre;
    private int ano;
    
    public ReporteTrimestral() {
        super();
        this.tipoReporte = "TRIMESTRAL";
    }
    
    public ReporteTrimestral(int trimestre, int ano) {
        this();
        this.trimestre = trimestre;
        this.ano = ano;
    }
    
    @Override
    public String generarContenido() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("=== REPORTE TRIMESTRAL ===\n");
        contenido.append("Empresa: ").append(empresa).append("\n");
        contenido.append("Trimestre: ").append(trimestre).append(" - Año: ").append(ano).append("\n");
        contenido.append("Fecha de generación: ").append(fechaGeneracion).append("\n");
        
        if (datos != null) {
            datos.forEach((key, value) -> 
                contenido.append(key).append(": ").append(value).append("\n")
            );
        }
        
        contenido.append("\nFirma autorizada: ").append(firmaAutorizada).append("\n");
        return contenido.toString();
    }
    
    public int getTrimestre() {
        return trimestre;
    }
    
    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }
    
    public int getAno() {
        return ano;
    }
    
    public void setAno(int ano) {
        this.ano = ano;
    }
}
