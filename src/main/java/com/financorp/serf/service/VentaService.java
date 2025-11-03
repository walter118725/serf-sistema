package com.financorp.serf.service;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.model.Producto;
import com.financorp.serf.model.Venta;
import com.financorp.serf.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
}