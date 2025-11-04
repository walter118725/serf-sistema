package com.financorp.serf.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.model.Producto;
import com.financorp.serf.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final ConfiguracionGlobal configuracion = ConfiguracionGlobal.getInstance();
    
    @Transactional
    public Producto registrarProducto(Producto producto) {
        // Convertir y almacenar el costo en EUR (moneda corporativa)
        BigDecimal costoEnEUR = configuracion.convertirAMonedaCorporativa(
            producto.getCostoImportacion(),
            producto.getMonedaOrigen()
        );
        
        // Establecer el precio sugerido basado en el costo convertido
        if (producto.getPrecioVentaSugerido() == null) {
            producto.setPrecioVentaSugerido(costoEnEUR.multiply(new BigDecimal("1.3"))); // Margen del 30%
        }
        
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
        if (productoId == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser null");
        }
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
    
    public Producto obtenerPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser null");
        }
        return productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }
    
    @Transactional
    public Producto actualizarProducto(Long id, Producto producto) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser null");
        }
        
        Producto productoExistente = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        
        // Actualizar los campos permitidos
        productoExistente.setCodigo(producto.getCodigo());
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setCategoria(producto.getCategoria());
        productoExistente.setProveedor(producto.getProveedor());
        productoExistente.setCostoImportacion(producto.getCostoImportacion());
        productoExistente.setPrecioVentaSugerido(producto.getPrecioVentaSugerido());
        productoExistente.setMonedaOrigen(producto.getMonedaOrigen());
        productoExistente.setDescripcionTecnica(producto.getDescripcionTecnica());
        
        // Actualizar stock inicial si es diferente (mantener stock actual si no se especifica)
        if (producto.getStockInicial() != null) {
            // Si el stock inicial cambió, ajustar el stock actual proporcionalmente
            int diferencia = producto.getStockInicial() - productoExistente.getStockInicial();
            productoExistente.setStockInicial(producto.getStockInicial());
            productoExistente.setStockActual(Math.max(0, productoExistente.getStockActual() + diferencia));
        }
        
        // Convertir y actualizar precio sugerido si cambió la moneda origen
        if (producto.getMonedaOrigen() != null && !producto.getMonedaOrigen().equals(productoExistente.getMonedaOrigen())) {
            BigDecimal costoEnEUR = configuracion.convertirAMonedaCorporativa(
                productoExistente.getCostoImportacion(),
                productoExistente.getMonedaOrigen()
            );
            // Opcionalmente actualizar el precio sugerido basado en el nuevo costo
            if (productoExistente.getPrecioVentaSugerido().equals(BigDecimal.ZERO)) {
                productoExistente.setPrecioVentaSugerido(costoEnEUR.multiply(new BigDecimal("1.3")));
            }
        }
        
        return productoRepository.save(productoExistente);
    }
    
    @Transactional
    public void eliminarProducto(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser null");
        }
        
        // Verificar que el producto existe antes de eliminarlo
        if (!productoRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        
        // Verificar si el producto tiene ventas asociadas
        // Si tiene ventas, no debería eliminarse (opcional: agregar esta validación)
        
        productoRepository.deleteById(id);
    }
}