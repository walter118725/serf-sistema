package com.financorp.serf.controller;

import com.financorp.serf.model.Venta;
import com.financorp.serf.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {
    
    private final VentaService ventaService;
    
    @GetMapping
    public ResponseEntity<List<Venta>> listarTodas() {
        List<Venta> ventas = ventaService.listarTodas();
        return ResponseEntity.ok(ventas);
    }
    
    @PostMapping
    public ResponseEntity<Venta> registrar(@Valid @RequestBody Venta venta) {
        try {
            // Establecer fecha de venta si no se proporciona
            if (venta.getFechaVenta() == null) {
                venta.setFechaVenta(LocalDateTime.now());
            }
            
            Venta ventaGuardada = ventaService.registrarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Venta>> obtenerPorPais(@PathVariable String pais) {
        List<Venta> ventas = ventaService.obtenerVentasPorPais(pais);
        return ResponseEntity.ok(ventas);
    }
    
    @GetMapping("/fecha")
    public ResponseEntity<List<Venta>> obtenerPorRangoFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Venta> ventas = ventaService.obtenerVentasPorRangoFecha(inicio, fin);
        return ResponseEntity.ok(ventas);
    }
}
