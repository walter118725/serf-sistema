package com.financorp.serf.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.financorp.serf.model.Producto;
import com.financorp.serf.service.ProductoService;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    public void testListarTodos() throws Exception {
        // Datos de prueba
        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setCodigo("P001");
        producto1.setNombre("Laptop Dell");
        producto1.setPrecioVentaSugerido(new BigDecimal("1500.00"));

        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setCodigo("P002");
        producto2.setNombre("Mouse Logitech");
        producto2.setPrecioVentaSugerido(new BigDecimal("25.00"));

        List<Producto> productos = Arrays.asList(producto1, producto2);

        when(productoService.listarTodos()).thenReturn(productos);

        // Ejecutar prueba
        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].codigo").value("P001"))
                .andExpect(jsonPath("$[1].codigo").value("P002"));
    }

    @Test
    public void testObtenerPorCodigo() throws Exception {
        // Datos de prueba
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setCodigo("P001");
        producto.setNombre("Laptop Dell");

        when(productoService.buscarPorCodigo("P001")).thenReturn(producto);

        // Ejecutar prueba
        mockMvc.perform(get("/api/productos/codigo/P001")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("P001"))
                .andExpect(jsonPath("$.nombre").value("Laptop Dell"));
    }
}