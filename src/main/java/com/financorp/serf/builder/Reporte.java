package com.financorp.serf.builder;

import com.financorp.serf.composite.ComponenteReporte;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase Reporte
 * 
 * Representa un reporte financiero completo
 * Contiene: título, fecha, país, secciones, gráficos, tablas, etc.
 */
public class Reporte {

    private String titulo;
    private LocalDate fecha;
    private String pais;
    private String tipo;  // VENTAS, INVENTARIO, CONSOLIDADO
    private List<ComponenteReporte> secciones;
    private List<String> graficos;
    private List<String> tablas;
    private String footer;
    private String autor;

    public Reporte() {
        this.secciones = new ArrayList<>();
        this.graficos = new ArrayList<>();
        this.tablas = new ArrayList<>();
        this.fecha = LocalDate.now();
    }

    public Reporte(String titulo, LocalDate fecha, String pais, String tipo, List<ComponenteReporte> secciones, List<String> graficos, List<String> tablas, String footer, String autor) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.pais = pais;
        this.tipo = tipo;
        this.secciones = secciones != null ? secciones : new ArrayList<>();
        this.graficos = graficos != null ? graficos : new ArrayList<>();
        this.tablas = tablas != null ? tablas : new ArrayList<>();
        this.footer = footer;
        this.autor = autor;
    }

    // Getters / Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public List<ComponenteReporte> getSecciones() { 
        return Collections.unmodifiableList(secciones); 
    }
    public void setSecciones(List<ComponenteReporte> secciones) { 
        this.secciones = secciones != null ? new ArrayList<>(secciones) : new ArrayList<>(); 
    }

    public List<String> getGraficos() { 
        return Collections.unmodifiableList(graficos); 
    }
    public void setGraficos(List<String> graficos) { 
        this.graficos = graficos != null ? new ArrayList<>(graficos) : new ArrayList<>(); 
    }

    public List<String> getTablas() { 
        return Collections.unmodifiableList(tablas); 
    }
    public void setTablas(List<String> tablas) { 
        this.tablas = tablas != null ? new ArrayList<>(tablas) : new ArrayList<>(); 
    }

    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String generarContenido() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("===========================================\n");
        contenido.append(String.format("REPORTE: %s\n", titulo));
        contenido.append(String.format("Fecha: %s | País: %s\n", fecha, pais));
        contenido.append("===========================================\n\n");
        for (ComponenteReporte seccion : secciones) {
            contenido.append(seccion.renderizar()).append("\n");
        }
        if (footer != null && !footer.isEmpty()) {
            contenido.append("\n---\n").append(footer);
        }
        return contenido.toString();
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String titulo;
        private LocalDate fecha;
        private String pais;
        private String tipo;
        private List<ComponenteReporte> secciones = new ArrayList<>();
        private List<String> graficos = new ArrayList<>();
        private List<String> tablas = new ArrayList<>();
        private String footer;
        private String autor;

        public Builder titulo(String titulo) { this.titulo = titulo; return this; }
        public Builder fecha(LocalDate fecha) { this.fecha = fecha; return this; }
        public Builder pais(String pais) { this.pais = pais; return this; }
        public Builder tipo(String tipo) { this.tipo = tipo; return this; }
        public Builder secciones(List<ComponenteReporte> secciones) { 
            this.secciones = secciones != null ? new ArrayList<>(secciones) : new ArrayList<>(); 
            return this; 
        }
        public Builder graficos(List<String> graficos) { 
            this.graficos = graficos != null ? new ArrayList<>(graficos) : new ArrayList<>(); 
            return this; 
        }
        public Builder tablas(List<String> tablas) { 
            this.tablas = tablas != null ? new ArrayList<>(tablas) : new ArrayList<>(); 
            return this; 
        }
        public Builder footer(String footer) { this.footer = footer; return this; }
        public Builder autor(String autor) { this.autor = autor; return this; }

        public Reporte build() { return new Reporte(titulo, fecha, pais, tipo, secciones, graficos, tablas, footer, autor); }
    }
}
