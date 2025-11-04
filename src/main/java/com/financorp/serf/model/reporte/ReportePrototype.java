package com.financorp.serf.model.reporte;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ReportePrototype {
    
    private final Map<String, Reporte> prototipos;
    
    public ReportePrototype() {
        this.prototipos = new HashMap<>();
        inicializarPrototipos();
    }
    
    private void inicializarPrototipos() {
        // Crear prototipos base
        ReporteMensual reporteMensualBase = new ReporteMensual();
        reporteMensualBase.setEmpresa("FinanCorp S.A.");
        reporteMensualBase.setFirmaAutorizada("Gerencia General");
        
        ReporteAnual reporteAnualBase = new ReporteAnual();
        reporteAnualBase.setEmpresa("FinanCorp S.A.");
        reporteAnualBase.setFirmaAutorizada("Gerencia General");
        
        ReporteTrimestral reporteTrimestraleBase = new ReporteTrimestral();
        reporteTrimestraleBase.setEmpresa("FinanCorp S.A.");
        reporteTrimestraleBase.setFirmaAutorizada("Gerencia General");
        
        prototipos.put("MENSUAL", reporteMensualBase);
        prototipos.put("ANUAL", reporteAnualBase);
        prototipos.put("TRIMESTRAL", reporteTrimestraleBase);
    }
    
    public Reporte obtenerPrototipo(String tipo) {
        try {
            Reporte prototipo = prototipos.get(tipo.toUpperCase());
            return prototipo != null ? prototipo.clone() : null;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Error al clonar el prototipo: " + tipo, e);
        }
    }
    
    public void agregarPrototipo(String tipo, Reporte reporte) {
        prototipos.put(tipo.toUpperCase(), reporte);
    }
}
