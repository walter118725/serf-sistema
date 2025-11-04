package com.financorp.serf.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.model.Venta;
import com.financorp.serf.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    private final ConfiguracionGlobal configuracion = ConfiguracionGlobal.getInstance();
    
    @Transactional
    public Venta registrarVenta(Venta venta) {
        // Convertir precio a EUR
        BigDecimal precioEnEUR = configuracion.convertirAMonedaCorporativa(
            venta.getPrecioUnitario(),
            venta.getMonedaLocal()
        );
        
        venta.setPrecioUnitarioEUR(precioEnEUR);
        venta.setTotalVentaEUR(precioEnEUR.multiply(new BigDecimal(venta.getCantidad())));
        
        // Actualizar stock
        productoService.actualizarStock(venta.getProducto().getId(), venta.getCantidad());
        
        return ventaRepository.save(venta);
    }
    
    public List<Venta> obtenerVentasPorPais(String pais) {
        return ventaRepository.findByPaisFilial(pais);
    }
    
    public List<Venta> obtenerVentasPorRangoFecha(LocalDateTime inicio, LocalDateTime fin) {
        return ventaRepository.findByFechaVentaBetween(inicio, fin);
    }
    
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }
    
    public java.util.Map<String, Object> obtenerEstadisticasGenerales() {
        List<Venta> todasLasVentas = ventaRepository.findAll();
        List<com.financorp.serf.model.Producto> todosLosProductos = productoService.listarTodos();
        
        // Calcular estadísticas
        long totalVentas = todasLasVentas.size();
        long totalProductos = todosLosProductos.size();
        
        BigDecimal ingresosTotales = todasLasVentas.stream()
            .map(Venta::getTotalVentaEUR)
            .filter(java.util.Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long productosActivos = todosLosProductos.stream()
            .mapToInt(com.financorp.serf.model.Producto::getStockActual)
            .sum();
        
        // Crear el mapa de estadísticas
        java.util.Map<String, Object> estadisticas = new java.util.HashMap<>();
        estadisticas.put("totalVentas", totalVentas);
        estadisticas.put("totalProductos", totalProductos);
        estadisticas.put("ingresosTotales", ingresosTotales);
        estadisticas.put("productosActivos", productosActivos);
        estadisticas.put("ventasDelMes", obtenerVentasDelMes());
        estadisticas.put("productosLowStock", productoService.obtenerProductosBajoStock(10).size());
        
        return estadisticas;
    }
    
    private long obtenerVentasDelMes() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = inicioMes.plusMonths(1).minusSeconds(1);
        return ventaRepository.findByFechaVentaBetween(inicioMes, finMes).size();
    }
}