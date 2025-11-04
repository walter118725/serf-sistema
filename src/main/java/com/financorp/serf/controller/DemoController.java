package com.financorp.serf.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.financorp.serf.demo.FlujoTrabajoDemo;

import lombok.RequiredArgsConstructor;

/**
 * Controlador para demostrar el flujo de trabajo completo del Sistema SERF
 * Implementa exactamente los 3 pasos especificados en el caso de estudio
 */
@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DemoController {
    
    private final FlujoTrabajoDemo flujoTrabajoDemo;
    
    /**
     * Ejecuta el flujo de trabajo completo del Sistema SERF
     * 1. Registro de producto importado desde Shenzhen, China (CNY → EUR)
     * 2. Registro de venta desde filial Perú (PEN → EUR) 
     * 3. Generación de reporte usando todos los patrones de diseño
     * 
     * @return Resultados completos del flujo de trabajo
     */
    @PostMapping("/flujo-completo")
    public ResponseEntity<Map<String, Object>> ejecutarFlujoCompleto() {
        try {
            Map<String, Object> resultados = flujoTrabajoDemo.ejecutarFlujoCompletoSERF();
            return ResponseEntity.ok(resultados);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Error en flujo de trabajo: " + e.getMessage()));
        }
    }
    
    /**
     * Ejecuta solo el Paso 1: Registro de producto importado
     */
    @PostMapping("/paso1-registro-producto")
    public ResponseEntity<?> ejecutarPaso1() {
        try {
            var resultado = flujoTrabajoDemo.ejecutarPaso1_RegistroProductoImportado();
            return ResponseEntity.ok(Map.of(
                "mensaje", "Paso 1 completado: Producto registrado desde China",
                "producto", resultado
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Error en Paso 1: " + e.getMessage()));
        }
    }
    
    /**
     * Información sobre el flujo de trabajo del Sistema SERF
     */
    @GetMapping("/flujo-info")
    public ResponseEntity<Map<String, Object>> obtenerInfoFlujo() {
        Map<String, Object> info = Map.of(
            "nombre", "Flujo de Trabajo Sistema SERF",
            "descripcion", "Demostración completa de los 3 pasos del workflow empresarial",
            "pasos", Map.of(
                "paso1", "Registro de productos importados desde China (CNY → EUR)",
                "paso2", "Registro de ventas desde filiales (moneda local → EUR)", 
                "paso3", "Generación de reportes con todos los patrones de diseño"
            ),
            "patrones", "Singleton → Prototype → Builder → Composite → Decorator → Facade",
            "seguridad", "Marca de agua + Firma digital SHA-256",
            "conversion_automatica", "CNY, PEN, USD, MXN, COP → EUR corporativo"
        );
        
        return ResponseEntity.ok(info);
    }
}