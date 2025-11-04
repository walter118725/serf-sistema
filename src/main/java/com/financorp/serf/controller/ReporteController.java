package com.financorp.serf.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financorp.serf.facade.ReporteFacade;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReporteController {
    
    private final ReporteFacade reporteFacade;
    
    @GetMapping("/ventas/mensual")
    public ResponseEntity<Map<String, Object>> reporteMensual(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            Map<String, Object> reporte = reporteFacade.generarReporteMensual(fecha);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/ventas/trimestral")
    public ResponseEntity<Map<String, Object>> reporteTrimestral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            Map<String, Object> reporte = reporteFacade.generarReporteTrimestral(fecha);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/ventas/anual")
    public ResponseEntity<Map<String, Object>> reporteAnual(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            Map<String, Object> reporte = reporteFacade.generarReporteAnual(fecha);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/productos/stock")
    public ResponseEntity<Map<String, Object>> reporteStock() {
        try {
            Map<String, Object> reporte = reporteFacade.generarReporteStock();
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
