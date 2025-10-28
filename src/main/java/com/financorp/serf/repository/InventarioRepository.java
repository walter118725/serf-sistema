package com.financorp.serf.repository;

import com.financorp.serf.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * REPOSITORIO de Inventario
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    /**
     * Buscar inventario de un país específico
     */
    List<Inventario> findByPais(String pais);
    
    /**
     * ALERTA: Buscar productos con stock bajo
     * Productos donde cantidad < stockMinimo
     * 
     * Ejemplo: Si tenemos 5 laptops pero el mínimo es 10, ¡ALERTA!
     */
    @Query("SELECT i FROM Inventario i WHERE i.cantidad < i.stockMinimo")
    List<Inventario> findProductosBajoStock();
    
    /**
     * Stock bajo en un país específico
     */
    @Query("SELECT i FROM Inventario i WHERE i.pais = :pais " +
           "AND i.cantidad < i.stockMinimo")
    List<Inventario> findProductosBajoStockPorPais(@Param("pais") String pais);
}
