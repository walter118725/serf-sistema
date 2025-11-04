package com.financorp.serf.service;

import com.financorp.serf.model.Producto;
import com.financorp.serf.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    public void testListarTodos() {
        // Datos de prueba
        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setCodigo("P001");
        producto1.setNombre("Laptop Dell");

        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setCodigo("P002");
        producto2.setNombre("Mouse Logitech");

        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(productoRepository.findAll()).thenReturn(productos);

        // Ejecutar prueba
        List<Producto> resultado = productoService.listarTodos();

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("P001", resultado.get(0).getCodigo());
        assertEquals("P002", resultado.get(1).getCodigo());
    }

    @Test
    public void testObtenerProductosBajoStock() {
        // Datos de prueba
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCodigo("P001");
        producto.setStockActual(5);

        List<Producto> productos = Arrays.asList(producto);

        when(productoRepository.findByStockActualLessThan(10)).thenReturn(productos);

        // Ejecutar prueba
        List<Producto> resultado = productoService.obtenerProductosBajoStock(10);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).getStockActual());
    }
}