package com.financorp.serf.service;

import com.financorp.serf.model.Producto;
import com.financorp.serf.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * SERVICIO de Productos
 * 
 * ¿Qué hace?
 * - Contiene la lógica de negocio para productos
 * - Es el intermediario entre el Controller y el Repository
 * 
 * ¿Por qué usamos servicios?
 * - Separa la lógica de negocio de los controladores
 * - Más fácil de probar y mantener
 * - Principio de Responsabilidad Única (SOLID)
 */
@Service  // Esta anotación dice: "Soy un servicio de Spring"
public class ServicioProductos {
    
    /**
     * @Autowired = Spring automáticamente inyecta el repository
     * No necesitamos hacer: new ProductoRepository()
     */
    @Autowired
    private ProductoRepository productoRepository;
    
    /**
     * Obtener todos los productos
     */
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }
    
    /**
     * Obtener un producto por su ID
     * Optional = Puede existir o no
     */
    public Optional<Producto> obtenerPorId(Long id) {
        return productoRepository.findById(id);
    }
    
    /**
     * Guardar o actualizar un producto
     * Si tiene ID, actualiza. Si no tiene ID, crea uno nuevo
     */
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }
    
    /**
     * Eliminar un producto por ID
     */
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
    
    /**
     * Buscar por categoría
     */
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    /**
     * Buscar por nombre (búsqueda parcial)
     */
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
}