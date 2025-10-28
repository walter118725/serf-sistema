package com.financorp.serf.controller;

import com.financorp.serf.model.Inventario;
import com.financorp.serf.service.ServicioInventario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR REST de Inventario
 */
@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {
    
    @Autowired
    private ServicioInventario servicioInventario;
    
    /**
     * GET: Obtener todo el inventario
     */
    @GetMapping
    public ResponseEntity<List<Inventario>> obtenerTodo() {
        return ResponseEntity.ok(servicioInventario.obtenerTodo());
    }
    
    /**
     * GET: Obtener inventario por país
     */
    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Inventario>> porPais(@PathVariable String pais) {
        return ResponseEntity.ok(servicioInventario.obtenerInventarioPorPais(pais));
    }
    
    /**
     * GET: Obtener alertas de productos con stock bajo
     * URL: http://localhost:8080/api/inventario/alertas
     */
    @GetMapping("/alertas")
    public ResponseEntity<Map<String, Object>> productosBajoStock() {
        List<Inventario> productos = servicioInventario.obtenerProductosBajoStock();
        
        Map<String, Object> response = new HashMap<>();
        response.put("alertas", productos);
        response.put("cantidad", productos.size());
        response.put("mensaje", productos.isEmpty() ? 
            "✅ No hay productos con stock bajo" : 
            "⚠️ Hay " + productos.size() + " productos que necesitan reabastecimiento");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT: Ajustar stock de un producto
     * URL: http://localhost:8080/api/inventario/1/ajustar?cantidad=10
     */
    @PutMapping("/{id}/ajustar")
    public ResponseEntity<Map<String, Object>> ajustarStock(
            @PathVariable Long id,
            @RequestParam int cantidad) {
        
        servicioInventario.actualizarStock(id, cantidad);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Stock ajustado correctamente");
        response.put("ajuste", cantidad);
        
        return ResponseEntity.ok(response);
    }
}
