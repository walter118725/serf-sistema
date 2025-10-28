package com.financorp.serf.service;

import com.financorp.serf.model.Inventario;
import com.financorp.serf.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * SERVICIO de Inventario
 */
@Service
public class ServicioInventario {
    
    @Autowired
    private InventarioRepository inventarioRepository;
    
    /**
     * Obtener todo el inventario
     */
    public List<Inventario> obtenerTodo() {
        return inventarioRepository.findAll();
    }
    
    /**
     * Guardar o actualizar inventario
     */
    public Inventario guardar(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }
    
    /**
     * Obtener inventario de un país
     */
    public List<Inventario> obtenerInventarioPorPais(String pais) {
        return inventarioRepository.findByPais(pais);
    }
    
    /**
     * Obtener productos con stock bajo (ALERTAS)
     * Estos son productos que necesitan reabastecimiento
     */
    public List<Inventario> obtenerProductosBajoStock() {
        return inventarioRepository.findProductosBajoStock();
    }
    
    /**
     * Obtener productos con stock bajo en un país específico
     */
    public List<Inventario> obtenerProductosBajoStockPorPais(String pais) {
        return inventarioRepository.findProductosBajoStockPorPais(pais);
    }
    
    /**
     * Actualizar el stock de un producto
     * La cantidad puede ser positiva (entrada) o negativa (salida)
     */
    public void actualizarStock(Long inventarioId, int cantidad) {
        inventarioRepository.findById(inventarioId).ifPresent(inv -> {
            inv.setCantidad(inv.getCantidad() + cantidad);
            inventarioRepository.save(inv);
        });
    }
}