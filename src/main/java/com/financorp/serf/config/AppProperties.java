package com.financorp.serf.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades personalizadas de la aplicación
 * 
 * Vincula las propiedades del archivo application.properties
 * con esta clase Java
 * 
 * Uso: @Autowired private AppProperties appProperties;
 *      String nombre = appProperties.getNombre();
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private String nombre;
    private String version;
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    @Override
    public String toString() {
        return "AppProperties{" +
                "nombre='" + nombre + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
