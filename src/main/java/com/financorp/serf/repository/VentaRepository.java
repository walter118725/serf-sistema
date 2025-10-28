package com.financorp.serf.repository;

import com.financorp.serf.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * REPOSITORIO de Ventas
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    /**
     * Buscar todas las ventas de un país
     */
    List<Venta> findByPais(String pais);
    
    /**
     * Buscar ventas entre dos fechas
     * Ejemplo: Ventas del 01/10/2025 al 31/10/2025
     */
    List<Venta> findByFechaBetween(LocalDate inicio, LocalDate fin);
    
    /**
     * Consulta personalizada usando @Query
     * Busca ventas de un país en un mes y año específico
     * 
     * JPQL = Java Persistence Query Language (como SQL pero con objetos)
     */
    @Query("SELECT v FROM Venta v WHERE v.pais = :pais " +
           "AND MONTH(v.fecha) = :mes AND YEAR(v.fecha) = :año")
    List<Venta> findByPaisAndMesAndAño(
        @Param("pais") String pais,
        @Param("mes") int mes,
        @Param("año") int año
    );
}