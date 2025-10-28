package com.financorp.serf.controller;

import com.financorp.serf.model.Producto;
import com.financorp.serf.service.ServicioProductos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CONTROLADOR REST de Productos
 * CRUD completo: Create, Read, Update, Delete
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    @Autowired
    private ServicioProductos servicioProductos;
    
    /**
     * GET: Obtener todos los productos
     * URL: http://localhost:8080/api/productos
     */
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(servicioProductos.obtenerTodos());
    }
    
    /**
     * GET: Obtener un producto por ID
     * URL: http://localhost:8080/api/productos/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return servicioProductos.obtenerPorId(id)
            .map(ResponseEntity::ok)  // Si existe, devuelve 200 OK
            .orElse(ResponseEntity.notFound().build());  // Si no, 404 Not Found
    }
    
    /**
     * POST: Crear un nuevo producto
     * URL: http://localhost:8080/api/productos
     * Body: JSON con los datos del producto
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody Producto producto) {
        Producto guardado = servicioProductos.guardar(producto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Producto creado exitosamente");
        response.put("producto", guardado);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * PUT: Actualizar un producto existente
     * URL: http://localhost:8080/api/productos/1
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto) {
        
        return servicioProductos.obtenerPorId(id)
            .map(p -> {
                producto.setId(id);  // Asegurar que use el ID correcto
                return ResponseEntity.ok(servicioProductos.guardar(producto));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * DELETE: Eliminar un producto
     * URL: http://localhost:8080/api/productos/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        servicioProductos.eliminar(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("mensaje", "Producto eliminado correctamente");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * GET: Buscar por nombre
     * URL: http://localhost:8080/api/productos/buscar?nombre=laptop
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscar(@RequestParam String nombre) {
        return ResponseEntity.ok(servicioProductos.buscarPorNombre(nombre));
    }
    
    /**
     * GET: Filtrar por categoría
     * URL: http://localhost:8080/api/productos/categoria/LAPTOPS
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> porCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(servicioProductos.buscarPorCategoria(categoria));
    }
}