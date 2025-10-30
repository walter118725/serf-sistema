// Dashboard JavaScript - SERF Sistema
// API Base URL
const API_BASE = '/api';

// Cargar datos al iniciar la página
document.addEventListener('DOMContentLoaded', function() {
    loadDashboardStats();
    loadAlertas();
    loadRecentSales();
    loadFeaturedProducts();
});

// Cargar estadísticas del dashboard
async function loadDashboardStats() {
    try {
        // Cargar productos
        const productosResponse = await fetch(`${API_BASE}/productos`);
        const productos = await productosResponse.json();
        document.getElementById('totalProductos').textContent = productos.length;

        // Cargar ventas
        const ventasResponse = await fetch(`${API_BASE}/ventas`);
        const ventas = await ventasResponse.json();
        document.getElementById('totalVentas').textContent = ventas.length;

        // Cargar inventario
        const inventarioResponse = await fetch(`${API_BASE}/inventario`);
        const inventario = await inventarioResponse.json();
        const totalItems = inventario.reduce((sum, item) => sum + item.cantidad, 0);
        document.getElementById('totalInventario').textContent = totalItems;

        // Cargar alertas
        const alertasResponse = await fetch(`${API_BASE}/inventario/alertas`);
        const alertas = await alertasResponse.json();
        document.getElementById('totalAlertas').textContent = alertas.length;

    } catch (error) {
        console.error('Error cargando estadísticas:', error);
        showError('Error al cargar las estadísticas del dashboard');
    }
}

// Cargar alertas de stock bajo
async function loadAlertas() {
    try {
        const response = await fetch(`${API_BASE}/inventario/alertas`);
        const alertas = await response.json();

        const alertasContent = document.getElementById('alertasContent');

        if (alertas.length === 0) {
            alertasContent.innerHTML = `
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    No hay alertas de stock bajo. Todos los productos tienen stock suficiente.
                </div>
            `;
            return;
        }

        let html = '<div class="table-container"><table class="table">';
        html += `
            <thead>
                <tr>
                    <th>Producto</th>
                    <th>Ubicación</th>
                    <th>País</th>
                    <th>Stock Actual</th>
                    <th>Stock Mínimo</th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
        `;

        alertas.forEach(alerta => {
            const deficit = alerta.stockMinimo - alerta.cantidad;
            html += `
                <tr>
                    <td><strong>${alerta.producto.nombre}</strong></td>
                    <td>${alerta.ubicacion}</td>
                    <td><span class="badge badge-info">${alerta.pais}</span></td>
                    <td><span class="text-danger"><strong>${alerta.cantidad}</strong></span></td>
                    <td>${alerta.stockMinimo}</td>
                    <td>
                        <span class="badge badge-danger">
                            <i class="fas fa-exclamation-triangle"></i>
                            Déficit: ${deficit} unidades
                        </span>
                    </td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        alertasContent.innerHTML = html;

    } catch (error) {
        console.error('Error cargando alertas:', error);
        document.getElementById('alertasContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                Error al cargar las alertas de stock.
            </div>
        `;
    }
}

// Cargar ventas recientes
async function loadRecentSales() {
    try {
        const response = await fetch(`${API_BASE}/ventas`);
        const ventas = await response.json();

        const ventasContent = document.getElementById('ventasContent');

        if (ventas.length === 0) {
            ventasContent.innerHTML = `
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i>
                    No hay ventas registradas todavía.
                </div>
            `;
            return;
        }

        // Ordenar por fecha (más recientes primero) y tomar las primeras 5
        const ventasRecientes = ventas
            .sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
            .slice(0, 5);

        let html = '<div class="table-container"><table class="table">';
        html += `
            <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>País</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
        `;

        ventasRecientes.forEach(venta => {
            const fecha = new Date(venta.fecha).toLocaleDateString('es-ES');
            html += `
                <tr>
                    <td>${fecha}</td>
                    <td><strong>${venta.producto.nombre}</strong></td>
                    <td>${venta.cantidad} unidades</td>
                    <td><span class="badge badge-info">${venta.pais}</span></td>
                    <td><strong>${venta.moneda} ${venta.total.toFixed(2)}</strong></td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        ventasContent.innerHTML = html;

    } catch (error) {
        console.error('Error cargando ventas:', error);
        document.getElementById('ventasContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                Error al cargar las ventas recientes.
            </div>
        `;
    }
}

// Cargar productos destacados
async function loadFeaturedProducts() {
    try {
        const response = await fetch(`${API_BASE}/productos`);
        const productos = await response.json();

        const productosContent = document.getElementById('productosContent');

        if (productos.length === 0) {
            productosContent.innerHTML = `
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i>
                    No hay productos en el catálogo.
                </div>
            `;
            return;
        }

        // Tomar los primeros 6 productos
        const productosDestacados = productos.slice(0, 6);

        let html = '<div class="table-container"><table class="table">';
        html += `
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Categoría</th>
                    <th>Fabricante</th>
                    <th>País de Origen</th>
                    <th>Precio</th>
                </tr>
            </thead>
            <tbody>
        `;

        productosDestacados.forEach(producto => {
            html += `
                <tr>
                    <td><strong>${producto.nombre}</strong></td>
                    <td><span class="badge badge-secondary">${producto.categoria}</span></td>
                    <td>${producto.fabricante}</td>
                    <td>${producto.paisOrigen}</td>
                    <td><strong>$${producto.precio.toFixed(2)}</strong></td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        productosContent.innerHTML = html;

    } catch (error) {
        console.error('Error cargando productos:', error);
        document.getElementById('productosContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                Error al cargar los productos.
            </div>
        `;
    }
}

// Función para mostrar errores
function showError(message) {
    console.error(message);
    // Aquí podrías agregar una notificación visual más elaborada
}
