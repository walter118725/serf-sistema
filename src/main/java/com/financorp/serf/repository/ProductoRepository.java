package com.financorp.serf.repository;

import com.financorp.serf.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * REPOSITORIO de Productos
 * 
 * ¿Qué hace?
 * - Permite guardar, buscar, actualizar y eliminar productos
 * - Spring hace 
 * 
 * Hereda de JpaRepository que nos da métodos gratis:
 * - findAll() = Buscar todos
 * - findById(id) = Buscar por ID
 * - save(producto) = Guardar/actualizar
 * - deleteById(id) = Eliminar
 * 
 * ¡Solo escribimos los nombres de los métodos y Spring hace la magia!
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    /**
     * Buscar productos por categoría
     * Spring automáticamente crea el SQL:
     * SELECT * FROM productos WHERE categoria = ?
     */
    List<Producto> findByCategoria(String categoria);
    
    /**
     * Buscar por país de origen
     */
    List<Producto> findByPaisOrigen(String paisOrigen);
    
    /**
     * Buscar por nombre (permite buscar parcial, sin importar mayúsculas)
     * Ejemplo: Si busco "laptop", encuentra "Laptop Dell Inspiron"
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}