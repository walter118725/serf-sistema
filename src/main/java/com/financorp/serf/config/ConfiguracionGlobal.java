package com.financorp.serf.config;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConfiguracionGlobal {
    
    // Instancia única (Singleton gestionado por Spring)
    private static ConfiguracionGlobal instancia;
    
    // Moneda corporativa
    private final String monedaCorporativa = "EUR";
    
    // Tasas de cambio (en producción vendría de una API)
    private final Map<String, BigDecimal> tasasCambio;
    
    // Configuración de reportes
    private String formatoFechaReportes = "dd/MM/yyyy";
    private String logoEmpresa = "FinanCorp S.A.";
    private String firmaDigitalAutorizada = "Gerencia General";
    
    // Constructor privado para Singleton
    private ConfiguracionGlobal() {
        tasasCambio = new HashMap<>();
        inicializarTasasCambio();
    }
    
    // Método para obtener la instancia única
    public static synchronized ConfiguracionGlobal getInstance() {
        if (instancia == null) {
            instancia = new ConfiguracionGlobal();
        }
        return instancia;
    }
    
    private void inicializarTasasCambio() {
        // Tasas de cambio a EUR (ejemplo)
        tasasCambio.put("PEN", new BigDecimal("0.24")); // 1 PEN = 0.24 EUR
        tasasCambio.put("CNY", new BigDecimal("0.13")); // 1 CNY = 0.13 EUR
        tasasCambio.put("USD", new BigDecimal("0.92")); // 1 USD = 0.92 EUR
        tasasCambio.put("MXN", new BigDecimal("0.050")); // 1 MXN = 0.050 EUR
        tasasCambio.put("COP", new BigDecimal("0.00021")); // 1 COP = 0.00021 EUR
        tasasCambio.put("EUR", BigDecimal.ONE); // 1 EUR = 1 EUR
    }
    
    // Método principal: conversión de moneda
    public BigDecimal convertirAMonedaCorporativa(BigDecimal monto, String monedaOrigen) {
        if (monedaOrigen.equals(monedaCorporativa)) {
            return monto;
        }
        
        BigDecimal tasaCambio = tasasCambio.get(monedaOrigen);
        if (tasaCambio == null) {
            throw new IllegalArgumentException("Moneda no soportada: " + monedaOrigen);
        }
        
        return monto.multiply(tasaCambio);
    }
    
    // Getters
    public String getMonedaCorporativa() {
        return monedaCorporativa;
    }
    
    public String getFormatoFechaReportes() {
        return formatoFechaReportes;
    }
    
    public String getLogoEmpresa() {
        return logoEmpresa;
    }
    
    public String getFirmaDigitalAutorizada() {
        return firmaDigitalAutorizada;
    }
    
    public Map<String, BigDecimal> getTasasCambio() {
        return new HashMap<>(tasasCambio);
    }
    
    // Setters (para actualizar configuración)
    public void setFormatoFechaReportes(String formato) {
        this.formatoFechaReportes = formato;
    }
    
    public void actualizarTasaCambio(String moneda, BigDecimal tasa) {
        tasasCambio.put(moneda, tasa);
    }
}