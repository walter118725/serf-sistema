package com.financorp.serf.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.financorp.serf.builder.ReporteBuilder;
import com.financorp.serf.config.ConfiguracionGlobal;
import com.financorp.serf.decorator.FirmaDigitalDecorator;
import com.financorp.serf.decorator.MarcaAguaDecorator;
import com.financorp.serf.decorator.ReporteBasico;
import com.financorp.serf.decorator.ReporteComponent;
import com.financorp.serf.facade.ReporteFacade;
import com.financorp.serf.model.Producto;
import com.financorp.serf.model.Venta;
import com.financorp.serf.model.reporte.Reporte;
import com.financorp.serf.model.reporte.ReportePrototype;
import com.financorp.serf.service.ProductoService;
import com.financorp.serf.service.VentaService;

import lombok.RequiredArgsConstructor;

/**
 * Demostración del Flujo de Trabajo Completo del Sistema SERF
 * Implementa exactamente los 3 pasos especificados en el caso de estudio
 */
@Component
@RequiredArgsConstructor
public class FlujoTrabajoDemo {
    
    private final ProductoService productoService;
    private final VentaService ventaService;
    private final ReporteFacade reporteFacade;
    private final ReporteBuilder reporteBuilder;
    private final ReportePrototype reportePrototype;
    
    /**
     * FLUJO COMPLETO: Demuestra los 3 pasos del workflow del Sistema SERF
     */
    public Map<String, Object> ejecutarFlujoCompletoSERF() {
        Map<String, Object> resultados = new HashMap<>();
        
        System.out.println("🚀 INICIANDO FLUJO DE TRABAJO SISTEMA SERF");
        System.out.println("==========================================");
        
        // PASO 1: Registro de producto importado desde China
        Producto smartphone = ejecutarPaso1_RegistroProductoImportado();
        resultados.put("paso1_producto", smartphone);
        
        // PASO 2: Registro de ventas desde filial Perú
        Venta ventaPeru = ejecutarPaso2_RegistroVentasFilial(smartphone);
        resultados.put("paso2_venta", ventaPeru);
        
        // PASO 3: Generación de reporte financiero usando todos los patrones
        Map<String, Object> reporteConsolidado = ejecutarPaso3_GeneracionReporteFinanciero();
        resultados.put("paso3_reporte", reporteConsolidado);
        
        // Resumen del flujo completo
        Map<String, Object> resumen = generarResumenFlujo(smartphone, ventaPeru, reporteConsolidado);
        resultados.put("resumen_flujo", resumen);
        
        System.out.println("✅ FLUJO DE TRABAJO SERF COMPLETADO EXITOSAMENTE");
        return resultados;
    }
    
    /**
     * PASO 1: Registro de producto importado
     * El área de compras registra smartphones desde Shenzhen, China
     * El sistema convierte CNY → EUR automáticamente
     */
    public Producto ejecutarPaso1_RegistroProductoImportado() {
        System.out.println("\n📦 PASO 1: REGISTRO DE PRODUCTO IMPORTADO");
        System.out.println("===========================================");
        
        // Crear producto smartphone importado desde Shenzhen, China
        Producto smartphone = new Producto();
        smartphone.setCodigo("SMT-SZ-2025-001");
        smartphone.setNombre("Smartphone X Pro");
        smartphone.setDescripcionTecnica("Smartphone 5G 256GB RAM 8GB desde Shenzhen, China. Pantalla OLED 6.7\", cámara 108MP, procesador Snapdragon 8 Gen 2");
        smartphone.setCategoria(Producto.CategoriaProducto.SMARTPHONE);
        smartphone.setProveedor("Shenzhen TechCorp Ltd.");
        smartphone.setFechaImportacion(LocalDate.now());
        
        // Costo de importación en CNY (Yuan Chino)
        BigDecimal costoImportacionCNY = new BigDecimal("1200.00"); // 1200 CNY por unidad
        smartphone.setCostoImportacion(costoImportacionCNY);
        smartphone.setMonedaOrigen("CNY");
        
        // Stock inicial del lote importado
        smartphone.setStockInicial(500); // Lote de 500 unidades
        
        System.out.println("📱 Producto: " + smartphone.getNombre());
        System.out.println("🏭 Proveedor: " + smartphone.getProveedor());
        System.out.println("💰 Costo importación: " + costoImportacionCNY + " CNY");
        
        // El ProductoService automáticamente convierte CNY → EUR usando ConfiguracionGlobal
        Producto productoRegistrado = productoService.registrarProducto(smartphone);
        
        // Verificar conversión automática
        ConfiguracionGlobal config = ConfiguracionGlobal.getInstance();
        BigDecimal costoEUR = config.convertirAMonedaCorporativa(costoImportacionCNY, "CNY");
        
        System.out.println("💱 Conversión automática CNY → EUR:");
        System.out.println("   💴 " + costoImportacionCNY + " CNY = " + costoEUR + " EUR");
        System.out.println("   📈 Tasa de cambio: 1 CNY = " + config.getTasasCambio().get("CNY") + " EUR");
        System.out.println("📦 Stock inicial: " + productoRegistrado.getStockActual() + " unidades");
        System.out.println("✅ Producto registrado exitosamente con ID: " + productoRegistrado.getId());
        
        return productoRegistrado;
    }
    
    /**
     * PASO 2: Registro de ventas
     * Filial de Perú vende 100 unidades en PEN
     * Sistema convierte automáticamente PEN → EUR
     */
    public Venta ejecutarPaso2_RegistroVentasFilial(Producto smartphone) {
        System.out.println("\n🛒 PASO 2: REGISTRO DE VENTAS FILIAL PERÚ");
        System.out.println("=========================================");
        
        // Crear venta desde filial de Perú
        Venta venta = new Venta();
        venta.setNumeroFactura("PE-2025-001234");
        venta.setFechaVenta(LocalDateTime.now());
        venta.setProducto(smartphone);
        venta.setCantidad(100); // 100 unidades vendidas
        
        // Precio de venta en PEN (Soles Peruanos)
        BigDecimal precioVentaPEN = new BigDecimal("800.00"); // 800 PEN por unidad
        venta.setPrecioUnitario(precioVentaPEN);
        venta.setMonedaLocal("PEN");
        venta.setPaisFilial("PERÚ");
        
        // Datos adicionales de la venta
        venta.setCliente("Distribuidora TechPeru S.A.C.");
        venta.setVendedorResponsable("Carlos Mendoza");
        venta.setMetodoPago(Venta.MetodoPago.TRANSFERENCIA);
        
        System.out.println("🇵🇪 Filial: " + venta.getPaisFilial());
        System.out.println("📱 Producto: " + smartphone.getNombre());
        System.out.println("📊 Cantidad vendida: " + venta.getCantidad() + " unidades");
        System.out.println("💰 Precio unitario: " + precioVentaPEN + " PEN");
        System.out.println("🏢 Cliente: " + venta.getCliente());
        
        // VentaService automáticamente:
        // 1. Convierte PEN → EUR usando ConfiguracionGlobal (Singleton)
        // 2. Actualiza stock del producto automáticamente
        Venta ventaRegistrada = ventaService.registrarVenta(venta);
        
        // Mostrar conversión automática y actualización de stock
        ConfiguracionGlobal config = ConfiguracionGlobal.getInstance();
        BigDecimal precioEUR = config.convertirAMonedaCorporativa(precioVentaPEN, "PEN");
        BigDecimal totalVentaEUR = precioEUR.multiply(new BigDecimal(venta.getCantidad()));
        
        System.out.println("💱 Conversión automática PEN → EUR:");
        System.out.println("   💴 " + precioVentaPEN + " PEN = " + precioEUR + " EUR por unidad");
        System.out.println("   📈 Tasa de cambio: 1 PEN = " + config.getTasasCambio().get("PEN") + " EUR");
        System.out.println("   💰 Total venta: " + totalVentaEUR + " EUR");
        
        // Verificar actualización automática de stock
        Producto productoActualizado = productoService.obtenerPorId(smartphone.getId());
        System.out.println("📦 Actualización automática de stock:");
        System.out.println("   📉 Stock anterior: " + smartphone.getStockActual() + " unidades");
        System.out.println("   📉 Stock actual: " + productoActualizado.getStockActual() + " unidades");
        System.out.println("   ➖ Unidades vendidas: " + venta.getCantidad());
        
        System.out.println("✅ Venta registrada exitosamente con ID: " + ventaRegistrada.getId());
        
        return ventaRegistrada;
    }
    
    /**
     * PASO 3: Generación de reporte financiero
     * Usa todos los patrones de diseño en secuencia:
     * ConfiguracionGlobal → Prototype → Builder → Composite → Decorator → Facade
     */
    public Map<String, Object> ejecutarPaso3_GeneracionReporteFinanciero() {
        System.out.println("\n📊 PASO 3: GENERACIÓN DE REPORTE FINANCIERO");
        System.out.println("===========================================");
        
        System.out.println("🎯 Área contable solicita: Reporte Mensual Consolidado");
        System.out.println("🔄 El sistema ejecutará todos los patrones de diseño:");
        
        // 1. SINGLETON: ConfiguracionGlobal para moneda y formato
        System.out.println("🔧 1. Consultando ConfiguracionGlobal (Singleton)...");
        ConfiguracionGlobal config = ConfiguracionGlobal.getInstance();
        System.out.println("   💱 Moneda corporativa: " + config.getMonedaCorporativa());
        System.out.println("   📅 Formato fecha: " + config.getFormatoFechaReportes());
        System.out.println("   🏢 Empresa: " + config.getLogoEmpresa());
        
        // 2. PROTOTYPE: Clonar plantilla de reporte
        System.out.println("🔄 2. Clonando plantilla con Prototype...");
        Reporte plantillaMensual = reportePrototype.obtenerPrototipo("MENSUAL");
        System.out.println("   📋 Plantilla clonada: " + plantillaMensual.getClass().getSimpleName());
        
        // 3. BUILDER: Armar contenido con datos de ventas, inventarios y costos
        System.out.println("🏗️ 3. Construyendo reporte con Builder...");
        Map<String, Object> datosReporte = new HashMap<>();
        datosReporte.put("ventas", ventaService.listarTodas());
        datosReporte.put("productos", productoService.listarTodos());
        datosReporte.put("monedaCorporativa", config.getMonedaCorporativa());
        
        Reporte reporteBase = reporteBuilder
            .crearReporteMensual()
            .conEmpresa(config.getLogoEmpresa())
            .conFirmaAutorizada(config.getFirmaDigitalAutorizada())
            .conFechaGeneracion(LocalDate.now())
            .conDatos(datosReporte)
            .construir();
        
        System.out.println("   📈 Datos incluidos: ventas, inventarios, costos de importación");
        System.out.println("   📋 Encabezados: " + reporteBase.getEmpresa());
        System.out.println("   ✍️ Firma autorizada: " + reporteBase.getFirmaAutorizada());
        
        // 4. COMPOSITE: Estructurar secciones (implementado en el servicio)
        System.out.println("🌳 4. Estructurando secciones con Composite...");
        System.out.println("   📊 Sección: Ingresos por país (filiales)");
        System.out.println("   📱 Sección: Ingresos por categoría de producto");
        System.out.println("   💰 Sección: Gastos de importación por proveedor");
        
        // 5. DECORATOR: Aplicar marca de agua y firma digital
        System.out.println("🎨 5. Aplicando seguridad con Decorator...");
        
        // Crear componente base del reporte
        ReporteComponent reporteBasico = new ReporteBasico(reporteBase);
        
        // Aplicar marca de agua
        ReporteComponent conMarcaAgua = new MarcaAguaDecorator(reporteBasico);
        System.out.println("   💧 Marca de agua aplicada: FinanCorp S.A. - CONFIDENCIAL");
        
        // Aplicar firma digital
        ReporteComponent conFirmaDigital = new FirmaDigitalDecorator(conMarcaAgua, config.getFirmaDigitalAutorizada());
        System.out.println("   🔐 Firma digital SHA-256 aplicada");
        
        // 6. FACADE: Entregar reporte final usando interfaz única
        System.out.println("🎭 6. Entregando reporte con Facade...");
        Map<String, Object> reporteFinal = reporteFacade.generarReporteMensual(LocalDate.now());
        
        // Agregar metadatos del proceso
        reporteFinal.put("procesoCompleto", true);
        reporteFinal.put("patronesUsados", "Singleton → Prototype → Builder → Composite → Decorator → Facade");
        reporteFinal.put("seguridadDocumental", conFirmaDigital.tieneSeguridad());
        reporteFinal.put("contenidoSeguro", conFirmaDigital.generarContenido());
        reporteFinal.put("metadatosSeguridad", conFirmaDigital.obtenerMetadatos());
        
        System.out.println("✅ Reporte entregado sin complejidad técnica para el usuario");
        System.out.println("🔒 Seguridad aplicada: Marca de agua + Firma SHA-256");
        System.out.println("📊 Formato estandarizado corporativo aplicado");
        
        return reporteFinal;
    }
    
    /**
     * Genera un resumen ejecutivo del flujo completo
     */
    private Map<String, Object> generarResumenFlujo(Producto smartphone, Venta venta, Map<String, Object> reporte) {
        Map<String, Object> resumen = new HashMap<>();
        
        ConfiguracionGlobal config = ConfiguracionGlobal.getInstance();
        
        // Conversiones realizadas
        BigDecimal costoEUR = config.convertirAMonedaCorporativa(smartphone.getCostoImportacion(), "CNY");
        BigDecimal ventaEUR = config.convertirAMonedaCorporativa(venta.getPrecioUnitario(), "PEN");
        
        resumen.put("flujoCompletado", true);
        resumen.put("fechaEjecucion", LocalDateTime.now());
        
        // Resumen Paso 1
        Map<String, Object> paso1 = new HashMap<>();
        paso1.put("accion", "Registro de producto desde Shenzhen, China");
        paso1.put("producto", smartphone.getNombre());
        paso1.put("costoOriginal", smartphone.getCostoImportacion() + " CNY");
        paso1.put("costoConvertido", costoEUR + " EUR");
        paso1.put("stockInicial", smartphone.getStockInicial());
        resumen.put("paso1_registro_producto", paso1);
        
        // Resumen Paso 2  
        Map<String, Object> paso2 = new HashMap<>();
        paso2.put("accion", "Venta desde filial Perú");
        paso2.put("cantidad", venta.getCantidad() + " unidades");
        paso2.put("precioOriginal", venta.getPrecioUnitario() + " PEN");
        paso2.put("precioConvertido", ventaEUR + " EUR");
        paso2.put("totalVenta", venta.getTotalVentaEUR() + " EUR");
        paso2.put("stockRestante", productoService.obtenerPorId(smartphone.getId()).getStockActual());
        resumen.put("paso2_registro_venta", paso2);
        
        // Resumen Paso 3
        Map<String, Object> paso3 = new HashMap<>();
        paso3.put("accion", "Reporte mensual consolidado");
        paso3.put("patronesUsados", 6);
        paso3.put("seguridadAplicada", true);
        paso3.put("formatoEstandarizado", true);
        paso3.put("interfazSimplificada", true);
        resumen.put("paso3_generacion_reporte", paso3);
        
        // Métricas del flujo
        resumen.put("conversionesAutomaticas", 2); // CNY→EUR, PEN→EUR
        resumen.put("patronesImplementados", 6);
        resumen.put("seguridadDocumental", true);
        resumen.put("monedaCorporativa", config.getMonedaCorporativa());
        
        return resumen;
    }
}