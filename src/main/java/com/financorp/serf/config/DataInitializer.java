package com.financorp.serf.config;

import com.financorp.serf.model.*;
import com.financorp.serf.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * INICIALIZADOR DE DATOS
 * 
 * ¿Qué hace?
 * - Se ejecuta AUTOMÁTICAMENTE cuando inicia la aplicación
 * - Crea datos de prueba en la base de datos
 * - Útil para desarrollo y demostración
 * 
 * CommandLineRunner = Se ejecuta después de que Spring Boot inicie
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private FilialRepository filialRepository;
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private InventarioRepository inventarioRepository;
    
    /**
     * Este método se ejecuta automáticamente al iniciar
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n🔄 Inicializando datos de prueba...\n");
        
        // ═══════════════════════════════════════
        // 1. CREAR FILIALES
        // ═══════════════════════════════════════
        
        Filial peru = Filial.builder()
            .nombre("FinanCorp Perú")
            .pais("PERU")
            .moneda("PEN")
            .direccion("Av. Javier Prado 123, San Isidro, Lima")
            .telefono("+51 1 234-5678")
            .gerenteNombre("Carlos Mendoza")
            .build();
        filialRepository.save(peru);
        
        Filial mexico = Filial.builder()
            .nombre("FinanCorp México")
            .pais("MEXICO")
            .moneda("MXN")
            .direccion("Paseo de la Reforma 456, CDMX")
            .telefono("+52 55 1234-5678")
            .gerenteNombre("Ana García")
            .build();
        filialRepository.save(mexico);
        
        Filial espana = Filial.builder()
            .nombre("FinanCorp España")
            .pais("ESPAÑA")
            .moneda("EUR")
            .direccion("Gran Vía 789, Madrid")
            .telefono("+34 91 123-4567")
            .gerenteNombre("José Martínez")
            .build();
        filialRepository.save(espana);
        
        System.out.println("✅ 3 filiales creadas: Perú, México, España");
        
        // ═══════════════════════════════════════
        // 2. CREAR PRODUCTOS
        // ═══════════════════════════════════════
        
        Producto laptop1 = Producto.builder()
            .nombre("Laptop Dell Inspiron 15")
            .categoria("LAPTOPS")
            .precio(new BigDecimal("850.00"))
            .fabricante("Dell")
            .paisOrigen("China")
            .descripcion("Laptop Core i5, 8GB RAM, 256GB SSD")
            .fechaImportacion(LocalDate.now().minusMonths(2))
            .build();
        productoRepository.save(laptop1);
        
        Producto laptop2 = Producto.builder()
            .nombre("Laptop HP Pavilion 14")
            .categoria("LAPTOPS")
            .precio(new BigDecimal("920.00"))
            .fabricante("HP")
            .paisOrigen("China")
            .descripcion("Laptop Core i7, 16GB RAM, 512GB SSD")
            .fechaImportacion(LocalDate.now().minusMonths(1))
            .build();
        productoRepository.save(laptop2);
        
        Producto smartphone1 = Producto.builder()
            .nombre("Smartphone Xiaomi Redmi Note 12")
            .categoria("SMARTPHONES")
            .precio(new BigDecimal("280.00"))
            .fabricante("Xiaomi")
            .paisOrigen("China")
            .descripcion("6.67\", 8GB RAM, 256GB, Cámara 50MP")
            .fechaImportacion(LocalDate.now().minusWeeks(3))
            .build();
        productoRepository.save(smartphone1);
        
        Producto smartphone2 = Producto.builder()
            .nombre("Smartphone Samsung Galaxy A54")
            .categoria("SMARTPHONES")
            .precio(new BigDecimal("450.00"))
            .fabricante("Samsung")
            .paisOrigen("China")
            .descripcion("6.4\", 8GB RAM, 256GB, Cámara 50MP")
            .fechaImportacion(LocalDate.now().minusWeeks(2))
            .build();
        productoRepository.save(smartphone2);
        
        Producto router = Producto.builder()
            .nombre("Router TP-Link AC1200")
            .categoria("REDES")
            .precio(new BigDecimal("45.00"))
            .fabricante("TP-Link")
            .paisOrigen("China")
            .descripcion("Router WiFi Dual Band, 4 antenas")
            .fechaImportacion(LocalDate.now().minusWeeks(2))
            .build();
        productoRepository.save(router);
        
        Producto teclado = Producto.builder()
            .nombre("Teclado Logitech K380")
            .categoria("ACCESORIOS")
            .precio(new BigDecimal("35.00"))
            .fabricante("Logitech")
            .paisOrigen("China")
            .descripcion("Teclado inalámbrico Bluetooth")
            .fechaImportacion(LocalDate.now().minusWeeks(1))
            .build();
        productoRepository.save(teclado);
        
        System.out.println("✅ 6 productos creados");
        
        // ═══════════════════════════════════════
        // 3. CREAR VENTAS
        // ═══════════════════════════════════════
        
        Venta venta1 = Venta.builder()
            .producto(laptop1)
            .cantidad(5)
            .fecha(LocalDate.now().minusDays(10))
            .pais("PERU")
            .total(new BigDecimal("4250.00"))
            .moneda("PEN")
            .build();
        ventaRepository.save(venta1);
        
        Venta venta2 = Venta.builder()
            .producto(smartphone1)
            .cantidad(15)
            .fecha(LocalDate.now().minusDays(5))
            .pais("MEXICO")
            .total(new BigDecimal("4200.00"))
            .moneda("MXN")
            .build();
        ventaRepository.save(venta2);
        
        Venta venta3 = Venta.builder()
            .producto(laptop2)
            .cantidad(3)
            .fecha(LocalDate.now().minusDays(2))
            .pais("ESPAÑA")
            .total(new BigDecimal("2760.00"))
            .moneda("EUR")
            .build();
        ventaRepository.save(venta3);
        
        Venta venta4 = Venta.builder()
            .producto(router)
            .cantidad(20)
            .fecha(LocalDate.now().minusDays(7))
            .pais("PERU")
            .total(new BigDecimal("900.00"))
            .moneda("PEN")
            .build();
        ventaRepository.save(venta4);
        
        Venta venta5 = Venta.builder()
            .producto(smartphone2)
            .cantidad(8)
            .fecha(LocalDate.now().minusDays(3))
            .pais("MEXICO")
            .total(new BigDecimal("3600.00"))
            .moneda("MXN")
            .build();
        ventaRepository.save(venta5);
        
        System.out.println("✅ 5 ventas registradas");
        
        // ═══════════════════════════════════════
        // 4. CREAR INVENTARIO
        // ═══════════════════════════════════════
        
        Inventario inv1 = Inventario.builder()
            .producto(laptop1)
            .cantidad(25)
            .ubicacion("Almacén Central Lima")
            .pais("PERU")
            .stockMinimo(10)
            .build();
        inventarioRepository.save(inv1);
        
        // Este tiene stock bajo (8 < 15) - GENERARÁ ALERTA
        Inventario inv2 = Inventario.builder()
            .producto(smartphone1)
            .cantidad(8)
            .ubicacion("Almacén CDMX")
            .pais("MEXICO")
            .stockMinimo(15)
            .build();
        inventarioRepository.save(inv2);
        
        Inventario inv3 = Inventario.builder()
            .producto(router)
            .cantidad(50)
            .ubicacion("Almacén Madrid")
            .pais("ESPAÑA")
            .stockMinimo(20)
            .build();
        inventarioRepository.save(inv3);
        
        Inventario inv4 = Inventario.builder()
            .producto(laptop2)
            .cantidad(18)
            .ubicacion("Almacén Central Lima")
            .pais("PERU")
            .stockMinimo(12)
            .build();
        inventarioRepository.save(inv4);
        
        // Este tiene stock bajo (5 < 10) - GENERARÁ ALERTA
        Inventario inv5 = Inventario.builder()
            .producto(teclado)
            .cantidad(5)
            .ubicacion("Almacén CDMX")
            .pais("MEXICO")
            .stockMinimo(10)
            .build();
        inventarioRepository.save(inv5);
        
        Inventario inv6 = Inventario.builder()
            .producto(smartphone2)
            .cantidad(30)
            .ubicacion("Almacén Madrid")
            .pais("ESPAÑA")
            .stockMinimo(15)
            .build();
        inventarioRepository.save(inv6);
        
        System.out.println("✅ 6 registros de inventario creados");
        
        // ═══════════════════════════════════════
        // RESUMEN FINAL
        // ═══════════════════════════════════════
        
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║   ✅ DATOS DE PRUEBA CARGADOS EXITOSAMENTE       ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println("📦 " + productoRepository.count() + " productos");
        System.out.println("🏢 " + filialRepository.count() + " filiales");
        System.out.println("💰 " + ventaRepository.count() + " ventas");
        System.out.println("📊 " + inventarioRepository.count() + " registros de inventario");
        System.out.println("⚠️  2 productos con stock bajo (alertas activas)");
        System.out.println("\n🌐 Puedes empezar a probar la API en:");
        System.out.println("   http://localhost:8080/api/productos");
        System.out.println("   http://localhost:8080/api/ventas");
        System.out.println("   http://localhost:8080/api/inventario/alertas");
        System.out.println("   http://localhost:8080/api/reportes/consolidado\n");
    }
}