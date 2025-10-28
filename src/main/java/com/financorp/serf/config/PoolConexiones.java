package com.financorp.serf.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ═══════ PATRÓN SINGLETON - Pool de Conexiones ═══════
 * 
 * ¿Qué hace?
 * - Mantiene un "pool" (grupo) de conexiones a la base de datos
 * - Las conexiones se reutilizan (no se crean cada vez)
 * 
 * ¿Por qué?
 * - Crear una conexión es lento y costoso
 * - Es mejor tener conexiones listas para usar
 * - Como un estacionamiento de taxis: los tomas cuando necesitas
 */
public class PoolConexiones {
    
    private static volatile PoolConexiones instance;
    private static final Lock lock = new ReentrantLock();
    
    // Cola de conexiones disponibles
    private BlockingQueue<Connection> conexionesDisponibles;
    private int maxConexiones;
    
    // Datos de conexión a H2
    private final String URL = "jdbc:h2:mem:serfdb";
    private final String USER = "sa";
    private final String PASSWORD = "";
    
    /**
     * Constructor privado - Crea el pool
     */
    private PoolConexiones() {
        ConfiguracionSistema config = ConfiguracionSistema.getInstance();
        this.maxConexiones = Integer.parseInt(
            config.getProperty("pool.conexiones.max", "10")
        );
        this.conexionesDisponibles = new LinkedBlockingQueue<>(maxConexiones);
        inicializarPool();
    }
    
    /**
     * Obtener la instancia única
     */
    public static PoolConexiones getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new PoolConexiones();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    /**
     * Crear todas las conexiones del pool al inicio
     */
    private void inicializarPool() {
        try {
            for (int i = 0; i < maxConexiones; i++) {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                conexionesDisponibles.offer(conn);
            }
            System.out.println("✅ Pool de " + maxConexiones + " conexiones creado");
        } catch (SQLException e) {
            System.err.println("❌ Error creando pool: " + e.getMessage());
        }
    }
    
    /**
     * Obtener una conexión del pool
     * Si no hay, espera hasta que se libere una
     */
    public Connection obtenerConexion() throws InterruptedException {
        return conexionesDisponibles.take();
    }
    
    /**
     * Devolver la conexión al pool para que otros la usen
     */
    public void liberarConexion(Connection conn) {
        if (conn != null) {
            conexionesDisponibles.offer(conn);
        }
    }
    
    /**
     * Ver cuántas conexiones están disponibles
     */
    public int getConexionesDisponibles() {
        return conexionesDisponibles.size();
    }
}