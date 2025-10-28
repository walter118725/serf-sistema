package com.financorp.serf;

import com.financorp.serf.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication  // Esta anotación dice: "Esto es una app Spring Boot"
@EnableConfigurationProperties(AppProperties.class)
public class SerfApplication {
    
    public static void main(String[] args) {
        // Inicia la aplicación
        SpringApplication.run(SerfApplication.class, args);
        
        // Mensajes bonitos en la consola
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   🚀 SISTEMA SERF INICIADO CORRECTAMENTE 🚀       ║");
        System.out.println("║   Sistema Empresarial de Reportes Financieros    ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        System.out.println("📊 API REST: http://localhost:8080");
        System.out.println("🗄️  Base de Datos H2: http://localhost:8080/h2-console");
        System.out.println("    Usuario: sa");
        System.out.println("    Password: (dejar vacío)\n");
    }
}