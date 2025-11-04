package com.financorp.serf.service;

import com.financorp.serf.model.Venta;
import com.financorp.serf.model.Producto;
import com.financorp.serf.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ReporteService {
    
    private final VentaRepository ventaRepository;
    private final ProductoService productoService;
    
    public Map<String, Object> generarReporteVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);
        
        List<Venta> ventas = ventaRepository.findByFechaVentaBetween(inicio, fin);
        
        BigDecimal totalVentas = ventas.stream()
            .map(Venta::getTotalVentaEUR)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        reporte.put("totalVentas", ventas.size());
        reporte.put("montoTotal", totalVentas);
        reporte.put("ventas", ventas);
        
        return reporte;
    }
    
    public Map<String, Object> generarReporteMensual(LocalDate fecha) {
        LocalDate inicioMes = fecha.withDayOfMonth(1);
        LocalDate finMes = fecha.with(TemporalAdjusters.lastDayOfMonth());
        
        return generarReporteVentasPorPeriodo(inicioMes, finMes);
    }
    
    public Map<String, Object> generarReporteTrimestral(LocalDate fecha) {
        int trimestre = (fecha.getMonthValue() - 1) / 3 + 1;
        LocalDate inicioTrimestre = LocalDate.of(fecha.getYear(), (trimestre - 1) * 3 + 1, 1);
        LocalDate finTrimestre = inicioTrimestre.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        
        return generarReporteVentasPorPeriodo(inicioTrimestre, finTrimestre);
    }
    
    public Map<String, Object> generarReporteAnual(LocalDate fecha) {
        LocalDate inicioAno = LocalDate.of(fecha.getYear(), 1, 1);
        LocalDate finAno = LocalDate.of(fecha.getYear(), 12, 31);
        
        return generarReporteVentasPorPeriodo(inicioAno, finAno);
    }
    
    public Map<String, Object> generarReporteStock() {
        List<Producto> productos = productoService.listarTodos();
        List<Producto> bajoStock = productoService.obtenerProductosBajoStock(10);
        
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("totalProductos", productos.size());
        reporte.put("productosBajoStock", bajoStock.size());
        reporte.put("productos", productos);
        reporte.put("alertasBajoStock", bajoStock);
        
        return reporte;
    }
}
