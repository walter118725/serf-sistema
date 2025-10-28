package com.financorp.serf.service;

import com.financorp.serf.model.Venta;
import com.financorp.serf.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * SERVICIO de Ventas
 */
@Service
public class ServicioVentas {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    /**
     * Obtener todas las ventas
     */
    public List<Venta> obtenerTodas() {
        return ventaRepository.findAll();
    }
    
    /**
     * Registrar una nueva venta
     */
    public Venta registrarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }
    
    /**
     * Obtener ventas de un país específico
     */
    public List<Venta> obtenerVentasPorPais(String pais) {
        return ventaRepository.findByPais(pais);
    }
    
    /**
     * Obtener ventas por país, mes y año
     * Útil para reportes mensuales
     */
    public List<Venta> obtenerVentasPorPaisYMes(String pais, int mes, int año) {
        return ventaRepository.findByPaisAndMesAndAño(pais, mes, año);
    }
    
    /**
     * Obtener ventas entre dos fechas
     */
    public List<Venta> obtenerVentasPorFecha(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
    
    /**
     * Calcular el total de ventas
     * Suma todos los montos de las ventas en la lista
     */
    public BigDecimal calcularTotalVentas(List<Venta> ventas) {
        return ventas.stream()
            .map(Venta::getTotal)  // Obtener el total de cada venta
            .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sumarlos todos
    }
}