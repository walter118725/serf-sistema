package com.financorp.serf.controller;

import com.financorp.serf.builder.Reporte;
import com.financorp.serf.facade.FacadeReporteFinanciero;
import com.financorp.serf.prototype.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * CONTROLADOR REST de Reportes
 * 
 * ¿Qué hace?
 * - Expone endpoints (URLs) para que otros sistemas o el frontend
 *   puedan solicitar reportes
 * - Maneja las peticiones HTTP (GET, POST, etc.)
 * 
 * @RestController = Esta clase maneja peticiones HTTP y devuelve JSON
 * @RequestMapping = Todas las URLs empiezan con /api/reportes
 * @CrossOrigin = Permite peticiones desde otros dominios
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {
    
    @Autowired
    private FacadeReporteFinanciero facade;
    
    /**
     * ENDPOINT: Generar reporte de ventas
     * URL: GET http://localhost:8080/api/reportes/ventas/PERU?mes=10&año=2025
     * 
     * @PathVariable = Lee el país de la URL
     * @RequestParam = Lee mes y año de los parámetros
     */
    @GetMapping("/ventas/{pais}")
    public ResponseEntity<Map<String, Object>> generarReporteVentas(
            @PathVariable String pais,
            @RequestParam(defaultValue = "1") int mes,
            @RequestParam(defaultValue = "2025") int año) {
        
        // Generar el reporte usando el Facade
        Reporte reporte = facade.generarReporteVentas(pais, mes, año);
        
        // Crear respuesta JSON
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Reporte generado correctamente");
        response.put("reporte", reporte);
        response.put("contenido", reporte.generarContenido());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT: Generar reporte de inventario
     * URL: GET http://localhost:8080/api/reportes/inventario/PERU
     */
    @GetMapping("/inventario/{pais}")
    public ResponseEntity<Map<String, Object>> generarReporteInventario(
            @PathVariable String pais) {
        
        Reporte reporte = facade.generarReporteInventario(pais);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("reporte", reporte);
        response.put("contenido", reporte.generarContenido());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT: Generar reporte consolidado
     * URL: GET http://localhost:8080/api/reportes/consolidado
     */
    @GetMapping("/consolidado")
    public ResponseEntity<Map<String, Object>> generarReporteConsolidado() {
        Reporte reporte = facade.generarReporteConsolidado();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Reporte consolidado generado");
        response.put("reporte", reporte);
        response.put("contenido", reporte.generarContenido());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT: Generar reporte con seguridad (Decoradores)
     * URL: POST http://localhost:8080/api/reportes/seguro?pais=PERU&firmar=true
     */
    @PostMapping("/seguro")
    public ResponseEntity<Map<String, Object>> generarReporteSeguro(
            @RequestParam String pais,
            @RequestParam(defaultValue = "false") boolean firmar,
            @RequestParam(defaultValue = "false") boolean marcaAgua,
            @RequestParam(defaultValue = "false") boolean encriptar) {
        
        // Generar reporte base
        Reporte reporte = facade.generarReporteVentas(pais, 10, 2025);
        
        // Aplicar decoradores de seguridad
        String contenido = facade.generarReporteConSeguridad(
            reporte, firmar, marcaAgua, encriptar
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("contenido", contenido);
        response.put("seguridad", Map.of(
            "firmaDigital", firmar,
            "marcaAgua", marcaAgua,
            "encriptado", encriptar
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * ENDPOINT: Clonar plantilla (Patrón Prototype)
     * URL: GET http://localhost:8080/api/reportes/plantilla/ventas/MEXICO
     */
    @GetMapping("/plantilla/{tipo}/{pais}")
    public ResponseEntity<Map<String, Object>> clonarPlantilla(
            @PathVariable String tipo,
            @PathVariable String pais) {
        
        PlantillaReporte plantillaOriginal;
        
        // Crear plantilla según el tipo
        if (tipo.equalsIgnoreCase("ventas")) {
            plantillaOriginal = new PlantillaVentas();
        } else {
            plantillaOriginal = new PlantillaInventario();
        }
        
        // Clonar y configurar para el país
        PlantillaReporte plantillaClonada = plantillaOriginal.clonar();
        plantillaClonada.configurarPais(pais);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("plantillaOriginal", plantillaOriginal.generarEstructura());
        response.put("plantillaClonada", plantillaClonada.generarEstructura());
        response.put("mensaje", "Plantilla clonada y configurada para " + pais);
        
        return ResponseEntity.ok(response);
    }
}