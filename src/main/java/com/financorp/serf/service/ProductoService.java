package com.financorp.serf.service;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.model.Producto;
import com.financorp.serf.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final ConfiguracionGlobal configuracion = ConfiguracionGlobal.getInstance();
    
    @Transactional
    public Producto registrarProducto(Producto producto) {
        // Convertir costo de importación a EUR
        BigDecimal costoEnEUR = configuracion.convertirAMonedaCorporativa(
            producto.getCostoImportacion(),
            producto.getMonedaOrigen()
        );
        
        producto.setStockActual(producto.getStockInicial());
        
        return productoRepository.save(producto);
    }
    
    public Producto buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + codigo));
    }
    
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }
    
    @Transactional
    public void actualizarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        int nuevoStock = producto.getStockActual() - cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        producto.setStockActual(nuevoStock);
        productoRepository.save(producto);
    }
    
    public List<Producto> obtenerProductosBajoStock(Integer limiteStock) {
        return productoRepository.findByStockActualLessThan(limiteStock);
    }
}