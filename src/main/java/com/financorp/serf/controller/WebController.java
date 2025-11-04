package com.financorp.serf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador web para servir las páginas HTML del frontend
 */
@Controller
public class WebController {
    

    
    /**
     * Dashboard principal del sistema
     * @return vista del dashboard
     */
    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    /**
     * Página de gestión de productos
     * @return vista de productos
     */
    @GetMapping("/admin/productos")
    public String productos() {
        return "productos";
    }
    
    /**
     * Página de gestión de ventas
     * @return vista de ventas
     */
    @GetMapping("/admin/ventas")
    public String ventas() {
        return "ventas";
    }
    
    /**
     * Página de reportes
     * @return vista de reportes
     */
    @GetMapping("/admin/reportes")
    public String reportes() {
        return "reportes";
    }
}