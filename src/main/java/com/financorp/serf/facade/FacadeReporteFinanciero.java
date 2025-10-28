package com.financorp.serf.facade;

import com.financorp.serf.builder.Reporte;
import com.financorp.serf.builder.ReporteBuilder;
import com.financorp.serf.composite.*;
import com.financorp.serf.decorator.*;
import com.financorp.serf.model.*;
import com.financorp.serf.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ═══════ PATRÓN FACADE ═══════
 * 
 * ¿Qué hace?
 * - Proporciona una interfaz SIMPLE para un sistema complejo
 * - Oculta la complejidad interna
 * 
 * ¿Por qué es útil?
 * - El usuario no necesita saber cómo funciona todo internamente
 * - Solo llama a métodos simples del Facade
 * - Reduce el acoplamiento
 * 
 * Ejemplo de la vida real:
 * Un control remoto de TV es un Facade:
 * - Presionas UN botón "ON"
 * - Internamente: verifica alimentación, inicia circuitos, carga canales...
 * - Tú solo ves: TV encendida ✅
 */
@Component  // Spring lo maneja como un bean
public class FacadeReporteFinanciero {
    
    // El Facade usa múltiples servicios
    @Autowired
    private ServicioVentas servicioVentas;
    
    @Autowired
    private ServicioInventario servicioInventario;
    
    /**
     * Generar reporte de ventas 
     * 
     * SIN Facade, el usuario tendría que:
     * 1. Obtener ventas del servicio
     * 2. Calcular totales
     * 3. Crear el builder
     * 4. Crear secciones con Composite
     * 5. Agregar cada sección
     * 6. Build del reporte
     * 
     * CON Facade: Solo llama a este método ✨
     */
    public Reporte generarReporteVentas(String pais, int mes, int año) {
        // 1. Obtener datos
        List<Venta> ventas = servicioVentas.obtenerVentasPorPaisYMes(pais, mes, año);
        BigDecimal totalVentas = servicioVentas.calcularTotalVentas(ventas);
        
        // 2. Construir reporte con Builder
        ReporteBuilder builder = new ReporteBuilder();
        Reporte reporte = builder
            .setTitulo("Reporte de Ventas - " + pais)
            .setFecha(LocalDate.now())
            .setPais(pais)
            .setTipo("VENTAS")
            .setAutor("Sistema SERF")
            .build();
        
        // 3. Crear estructura con Composite
        Seccion resumen = new Seccion("RESUMEN EJECUTIVO");
        resumen.setContenido(String.format(
            "Total de ventas: $%s | Transacciones: %d", 
            totalVentas, ventas.size()
        ));
        reporte.getSecciones().add(resumen);
        
        // 4. Detalle de ventas
        Seccion detalle = new Seccion("DETALLE DE VENTAS");
        for (Venta venta : ventas) {
            Subseccion item = new Subseccion(
                venta.getProducto().getNombre(),
                String.format("Cantidad: %d | Total: $%s", 
                    venta.getCantidad(), venta.getTotal())
            );
            detalle.agregar(item);
        }
        reporte.getSecciones().add(detalle);
        
        reporte.setFooter("Generado por SERF - FinanCorp S.A.");
        
        return reporte;
    }
    
    /**
     * Generar reporte de inventario
     */
    public Reporte generarReporteInventario(String pais) {
        List<Inventario> inventarios = servicioInventario.obtenerInventarioPorPais(pais);
        
        ReporteBuilder builder = new ReporteBuilder();
        Reporte reporte = builder
            .setTitulo("Reporte de Inventario - " + pais)
            .setFecha(LocalDate.now())
            .setPais(pais)
            .setTipo("INVENTARIO")
            .build();
        
        // Stock total
        Seccion stockTotal = new Seccion("STOCK TOTAL");
        int totalProductos = inventarios.size();
        int totalUnidades = inventarios.stream()
            .mapToInt(Inventario::getCantidad)
            .sum();
        stockTotal.setContenido(String.format(
            "Productos: %d | Unidades: %d", 
            totalProductos, totalUnidades
        ));
        reporte.getSecciones().add(stockTotal);
        
        // Alertas de stock bajo
        Seccion alertas = new Seccion("⚠️ ALERTAS DE STOCK BAJO");
        for (Inventario inv : inventarios) {
            if (inv.getCantidad() < inv.getStockMinimo()) {
                Subseccion alerta = new Subseccion(
                    "⚠️ " + inv.getProducto().getNombre(),
                    String.format("Stock: %d | Mínimo requerido: %d", 
                        inv.getCantidad(), inv.getStockMinimo())
                );
                alertas.agregar(alerta);
            }
        }
        reporte.getSecciones().add(alertas);
        
        return reporte;
    }
    
    /**
     * Generar reporte consolidado de TODAS las filiales
     */
    public Reporte generarReporteConsolidado() {
        ReporteBuilder builder = new ReporteBuilder();
        Reporte reporte = builder
            .setTitulo("REPORTE CONSOLIDADO CORPORATIVO")
            .setFecha(LocalDate.now())
            .setPais("GLOBAL")
            .setTipo("CONSOLIDADO")
            .build();
        
        Seccion introduccion = new Seccion("RESUMEN CORPORATIVO");
        introduccion.setContenido("Consolidación de operaciones de todas las filiales");
        reporte.getSecciones().add(introduccion);
        
        // Agregar sección por cada país
        List<String> paises = List.of("PERU", "MEXICO", "ESPAÑA");
        for (String pais : paises) {
            Seccion seccionPais = new Seccion("Filial: " + pais, 2);
            List<Venta> ventas = servicioVentas.obtenerVentasPorPais(pais);
            BigDecimal total = servicioVentas.calcularTotalVentas(ventas);
            seccionPais.setContenido(String.format(
                "Ventas: $%s | Transacciones: %d", 
                total, ventas.size()
            ));
            reporte.getSecciones().add(seccionPais);
        }
        
        return reporte;
    }
    
    /**
     * Aplicar decoradores de seguridad al reporte
     */
    public String generarReporteConSeguridad(Reporte reporte, 
                                            boolean firmar, 
                                            boolean marcaAgua, 
                                            boolean encriptar) {
        String resultado = reporte.generarContenido();
        
        // Aplicar firma digital si se solicita
        if (firmar) {
            DecoradorReporte decorado = new ReporteConFirmaDigital(reporte);
            resultado = decorado.generar();
        }
        
        // Aplicar marca de agua si se solicita
        if (marcaAgua) {
            DecoradorReporte decorado = new ReporteConMarcaAgua(reporte);
            resultado = decorado.generar();
        }
        
        // Encriptar si se solicita
        if (encriptar) {
            DecoradorReporte decorado = new ReporteEncriptado(reporte);
            resultado = decorado.generar();
        }
        
        return resultado;
    }
}

/*
 * ═══ VENTAJAS DEL PATRÓN FACADE ═══
 * 
 * SIN Facade (complejo):
 * List<Venta> ventas = ventaRepo.findByPaisAndMes(...);
 * BigDecimal total = calcular(ventas);
 * ReporteBuilder builder = new ReporteBuilder();
 * Reporte r = builder.setTitulo(...).setPais(...).build();
 * Seccion s = new Seccion("Resumen");
 * s.setContenido(...);
 * r.getSecciones().add(s);
 * ... 20 líneas más ...
 * 
 * CON Facade (simple):
 * Reporte reporte = facade.generarReporteVentas("PERU", 10, 2025);
 * 
 * ¡Una línea en vez de 20! ✨
 */