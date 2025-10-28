package com.financorp.serf.controller;

import com.financorp.serf.model.Venta;
import com.financorp.serf.service.ServicioVentas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR REST de Ventas
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {
    
    @Autowired
    private ServicioVentas servicioVentas;
    
    /**
     * GET: Obtener todas las ventas
     */
    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodas() {
        return ResponseEntity.ok(servicioVentas.obtenerTodas());
    }
    
    /**
     * POST: Registrar una nueva venta
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Venta venta) {
        Venta registrada = servicioVentas.registrarVenta(venta);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Venta registrada exitosamente");
        response.put("venta", registrada);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET: Obtener ventas por país
     * URL: http://localhost:8080/api/ventas/pais/PERU
     */
    @GetMapping("/pais/{pais}")
    public ResponseEntity<Map<String, Object>> porPais(@PathVariable String pais) {
        List<Venta> ventas = servicioVentas.obtenerVentasPorPais(pais);
        BigDecimal total = servicioVentas.calcularTotalVentas(ventas);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ventas", ventas);
        response.put("total", total);
        response.put("cantidad", ventas.size());
        
        return ResponseEntity.ok(response);
    }
}