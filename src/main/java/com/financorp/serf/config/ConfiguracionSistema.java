package com.financorp.serf.config;

import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ═══════ PATRÓN SINGLETON ═══════
 * 
 * ¿Qué hace?
 * - Garantiza que solo haya UNA instancia de configuración en toda la app
 * - Es como tener un manual único que todos consultan
 * 
 * ¿Por qué es útil?
 * - Evita tener configuraciones diferentes en distintas partes
 * - Ahorra memoria (solo una instancia)
 * - Todos ven la misma configuración
 */
public class ConfiguracionSistema {
    
    // La ÚNICA instancia (volatile = visible para todos los threads)
    private static volatile ConfiguracionSistema instance;
    
    // Lock para evitar que 2 threads creen la instancia al mismo tiempo
    private static final Lock lock = new ReentrantLock();
    
    // Aquí guardamos todas las configuraciones
    private Properties properties;
    
    /**
     * Constructor PRIVADO
     * Nadie puede hacer: new ConfiguracionSistema()
     * Solo se usa getInstance()
     */
    private ConfiguracionSistema() {
        properties = new Properties();
        cargarConfiguracion();
        System.out.println("✅ Configuración del sistema cargada");
    }
    
    /**
     * MÉTODO PRINCIPAL: Obtener la instancia única
     * 
     * Usa "Double-Checked Locking" para ser thread-safe y rápido
     */
    public static ConfiguracionSistema getInstance() {
        // Primera verificación (sin bloqueo, rápida)
        if (instance == null) {
            // Bloquear para crear la instancia
            lock.lock();
            try {
                // Segunda verificación (por seguridad)
                if (instance == null) {
                    instance = new ConfiguracionSistema();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * Cargar configuraciones por defecto
     */
    private void cargarConfiguracion() {
        properties.setProperty("app.nombre", "SERF - FinanCorp");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.idioma", "es");
        properties.setProperty("app.moneda.default", "USD");
        properties.setProperty("pool.conexiones.max", "10");
    }
    
    // Métodos para usar las configuraciones
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}