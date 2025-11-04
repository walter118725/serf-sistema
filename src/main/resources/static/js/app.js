// ===== CONFIGURACIÓN GLOBAL =====
const API_BASE_URL = '/api';
let currentSection = 'dashboard';
let chartsInitialized = false;
let ventasChart = null;

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
    setupEventListeners();
    showSection('dashboard');
});

function initializeApp() {
    // Configurar fechas por defecto
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    
    const fechaInicio = document.getElementById('fecha-inicio');
    const fechaFin = document.getElementById('fecha-fin');
    
    if (fechaInicio) fechaInicio.value = formatDate(firstDay);
    if (fechaFin) fechaFin.value = formatDate(today);
}

// ===== EVENT LISTENERS =====
function setupEventListeners() {
    // Navegación
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const section = link.getAttribute('href').substring(1);
            showSection(section);
        });
    });
    
    // Búsqueda de productos
    const searchProductos = document.getElementById('search-productos');
    if (searchProductos) {
        searchProductos.addEventListener('input', debounce(searchProducts, 300));
    }
    
    // Filtro de categoría
    const filterCategoria = document.getElementById('filter-categoria');
    if (filterCategoria) {
        filterCategoria.addEventListener('change', searchProducts);
    }
    
    // Búsqueda de ventas
    const searchVentas = document.getElementById('search-ventas');
    if (searchVentas) {
        searchVentas.addEventListener('input', debounce(searchSales, 300));
    }
    
    // Cerrar modales con ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeAllModals();
        }
    });
    
    // Cerrar modales al hacer click fuera
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                closeModal(modal.id);
            }
        });
    });
}

// ===== NAVEGACIÓN =====
function showSection(sectionName) {
    // Actualizar navegación activa
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });
    document.querySelector(`[href="#${sectionName}"]`).classList.add('active');
    
    // Mostrar sección
    document.querySelectorAll('.content-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionName).classList.add('active');
    
    currentSection = sectionName;
    
    // Cargar datos según la sección
    switch(sectionName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'productos':
            loadProductos();
            break;
        case 'ventas':
            loadVentas();
            break;
        case 'reportes':
            // Los reportes se cargan bajo demanda
            break;
    }
}

// ===== DASHBOARD =====
async function loadDashboardData() {
    try {
        showLoading(true);
        
        // Cargar estadísticas generales
        const [productos, ventas, stockBajo] = await Promise.all([
            fetchAPI('/productos'),
            fetchAPI('/ventas'),
            fetchAPI('/productos/stock-bajo')
        ]);
        
        // Actualizar contadores
        updateDashboardStats(productos, ventas, stockBajo);
        
        // Cargar actividad reciente
        loadRecentActivity(ventas);
        
        // Inicializar gráficos
        if (!chartsInitialized) {
            initializeCharts(ventas);
            chartsInitialized = true;
        }
        
    } catch (error) {
        console.error('Error cargando dashboard:', error);
        showToast('Error cargando los datos del dashboard', 'error');
    } finally {
        showLoading(false);
    }
}

function updateDashboardStats(productos, ventas, stockBajo) {
    // Total productos
    document.getElementById('total-productos').textContent = productos.length;
    
    // Total ventas del mes
    const ventasDelMes = ventas.filter(venta => {
        const fechaVenta = new Date(venta.fechaVenta);
        const hoy = new Date();
        return fechaVenta.getMonth() === hoy.getMonth() && 
               fechaVenta.getFullYear() === hoy.getFullYear();
    });
    document.getElementById('total-ventas').textContent = ventasDelMes.length;
    
    // Ingresos totales en EUR
    const ingresosTotales = ventasDelMes.reduce((sum, venta) => {
        return sum + (venta.totalVentaEUR || 0);
    }, 0);
    document.getElementById('ingresos-totales').textContent = `€${ingresosTotales.toLocaleString('es-ES', {minimumFractionDigits: 2})}`;
    
    // Productos con stock bajo
    document.getElementById('stock-bajo').textContent = stockBajo.length;
}

function loadRecentActivity(ventas) {
    const activityContainer = document.getElementById('recent-activity');
    
    // Obtener las 5 ventas más recientes
    const recentSales = ventas
        .sort((a, b) => new Date(b.fechaVenta) - new Date(a.fechaVenta))
        .slice(0, 5);
    
    activityContainer.innerHTML = recentSales.map(venta => `
        <div class="activity-item">
            <div class="activity-icon" style="background-color: var(--success-color); color: white;">
                <i class="fas fa-shopping-cart"></i>
            </div>
            <div class="activity-content">
                <h4>Nueva venta registrada</h4>
                <p>${venta.cliente || 'Cliente no especificado'} - €${(venta.totalVentaEUR || 0).toFixed(2)}</p>
                <p class="text-xs text-gray">${formatDateTime(venta.fechaVenta)}</p>
            </div>
        </div>
    `).join('');
}

function initializeCharts(ventas) {
    const ctx = document.getElementById('ventasChart');
    if (!ctx) return;
    
    // Preparar datos para el gráfico
    const ventasPorMes = prepareChartData(ventas);
    
    ventasChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ventasPorMes.labels,
            datasets: [{
                label: 'Ventas (EUR)',
                data: ventasPorMes.data,
                borderColor: '#2563eb',
                backgroundColor: 'rgba(37, 99, 235, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function(value) {
                            return '€' + value.toLocaleString('es-ES');
                        }
                    }
                }
            }
        }
    });
}

function prepareChartData(ventas) {
    const monthlyData = {};
    const currentYear = new Date().getFullYear();
    
    // Inicializar meses
    for (let i = 0; i < 12; i++) {
        const monthName = new Date(currentYear, i, 1).toLocaleDateString('es-ES', { month: 'short' });
        monthlyData[monthName] = 0;
    }
    
    // Agregar datos de ventas
    ventas.forEach(venta => {
        const fecha = new Date(venta.fechaVenta);
        if (fecha.getFullYear() === currentYear) {
            const monthName = fecha.toLocaleDateString('es-ES', { month: 'short' });
            monthlyData[monthName] += (venta.totalVentaEUR || 0);
        }
    });
    
    return {
        labels: Object.keys(monthlyData),
        data: Object.values(monthlyData)
    };
}

// ===== PRODUCTOS =====
async function loadProductos() {
    try {
        showLoading(true);
        const productos = await fetchAPI('/productos');
        displayProductos(productos);
    } catch (error) {
        console.error('Error cargando productos:', error);
        showToast('Error cargando los productos', 'error');
    } finally {
        showLoading(false);
    }
}

function displayProductos(productos) {
    const tbody = document.getElementById('productos-tbody');
    
    tbody.innerHTML = productos.map(producto => `
        <tr>
            <td><span class="font-semibold">${producto.codigo}</span></td>
            <td>${producto.nombre}</td>
            <td><span class="badge info">${producto.categoria}</span></td>
            <td>
                <span class="badge ${producto.stockActual < 10 ? 'danger' : 'success'}">
                    ${producto.stockActual}
                </span>
            </td>
            <td>€${producto.precioVentaSugerido.toFixed(2)}</td>
            <td>
                <button class="btn btn-sm btn-outline" onclick="editProduct(${producto.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteProduct(${producto.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

async function searchProducts() {
    const searchTerm = document.getElementById('search-productos').value;
    const categoria = document.getElementById('filter-categoria').value;
    
    try {
        let productos = await fetchAPI('/productos');
        
        // Filtrar por búsqueda
        if (searchTerm) {
            productos = productos.filter(producto => 
                producto.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
                producto.codigo.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }
        
        // Filtrar por categoría
        if (categoria) {
            productos = productos.filter(producto => producto.categoria === categoria);
        }
        
        displayProductos(productos);
    } catch (error) {
        console.error('Error buscando productos:', error);
        showToast('Error en la búsqueda', 'error');
    }
}

// ===== VENTAS =====
async function loadVentas() {
    try {
        showLoading(true);
        const ventas = await fetchAPI('/ventas');
        displayVentas(ventas);
    } catch (error) {
        console.error('Error cargando ventas:', error);
        showToast('Error cargando las ventas', 'error');
    } finally {
        showLoading(false);
    }
}

function displayVentas(ventas) {
    const tbody = document.getElementById('ventas-tbody');
    
    tbody.innerHTML = ventas.map(venta => `
        <tr>
            <td><span class="font-semibold">${venta.numeroFactura}</span></td>
            <td>${formatDate(venta.fechaVenta)}</td>
            <td>${venta.cliente || 'No especificado'}</td>
            <td>${venta.producto?.nombre || 'Producto eliminado'}</td>
            <td>${venta.cantidad}</td>
            <td>€${(venta.totalVentaEUR || 0).toFixed(2)}</td>
            <td><span class="badge info">${venta.paisFilial}</span></td>
        </tr>
    `).join('');
}

async function searchSales() {
    const searchTerm = document.getElementById('search-ventas').value;
    
    try {
        let ventas = await fetchAPI('/ventas');
        
        if (searchTerm) {
            ventas = ventas.filter(venta => 
                (venta.cliente && venta.cliente.toLowerCase().includes(searchTerm.toLowerCase())) ||
                venta.numeroFactura.toLowerCase().includes(searchTerm.toLowerCase())
            );
        }
        
        displayVentas(ventas);
    } catch (error) {
        console.error('Error buscando ventas:', error);
        showToast('Error en la búsqueda', 'error');
    }
}

async function filtrarVentas() {
    const fechaInicio = document.getElementById('fecha-inicio').value;
    const fechaFin = document.getElementById('fecha-fin').value;
    
    if (!fechaInicio || !fechaFin) {
        showToast('Selecciona ambas fechas para filtrar', 'warning');
        return;
    }
    
    try {
        showLoading(true);
        const url = `/ventas/fecha?inicio=${fechaInicio}&fin=${fechaFin}`;
        const ventas = await fetchAPI(url);
        displayVentas(ventas);
        showToast(`Se encontraron ${ventas.length} ventas en el período seleccionado`, 'success');
    } catch (error) {
        console.error('Error filtrando ventas:', error);
        showToast('Error aplicando el filtro', 'error');
    } finally {
        showLoading(false);
    }
}

// ===== REPORTES =====
async function generarReporte(tipo) {
    try {
        showLoading(true);
        let endpoint = '';
        let title = '';
        let params = '';
        
        // Usar la fecha actual como parámetro por defecto
        const fechaActual = new Date().toISOString().split('T')[0];
        
        switch(tipo) {
            case 'mensual':
                endpoint = '/reportes/ventas/mensual';
                params = `?fecha=${fechaActual}`;
                title = 'Reporte Mensual de Ventas';
                break;
            case 'trimestral':
                endpoint = '/reportes/ventas/trimestral';
                params = `?fecha=${fechaActual}`;
                title = 'Reporte Trimestral de Ventas';
                break;
            case 'anual':
                endpoint = '/reportes/ventas/anual';
                params = `?fecha=${fechaActual}`;
                title = 'Reporte Anual de Ventas';
                break;
            case 'stock':
                endpoint = '/reportes/productos/stock';
                title = 'Reporte de Inventarios';
                break;
            case 'ventas':
                // Mostrar análisis de ventas del último mes
                endpoint = '/reportes/ventas/mensual';
                params = `?fecha=${fechaActual}`;
                title = 'Análisis de Ventas';
                break;
            case 'top-productos':
                // Generar ranking basado en ventas reales
                endpoint = '/reportes/productos/top-vendidos';
                title = 'Productos Más Vendidos';
                break;
        }
        
        const reporte = await fetchAPI(endpoint + params);
        displayReporte(reporte, title, tipo);
        
    } catch (error) {
        console.error('Error generando reporte:', error);
        showToast('Error generando el reporte. Verifique que hay datos disponibles.', 'error');
    } finally {
        showLoading(false);
    }
}

function displayReporte(reporte, title, tipo) {
    const resultsContainer = document.getElementById('report-results');
    
    let contenidoEspecifico = '';
    
    if (tipo === 'stock') {
        // Mostrar reporte de inventarios
        contenidoEspecifico = `
            <div class="report-section">
                <h4>Estadísticas de Inventario</h4>
                <div class="stats-grid">
                    <div class="report-stat">
                        <h5>Total de Productos</h5>
                        <p class="stat-number">${reporte.totalProductos || 0}</p>
                    </div>
                    <div class="report-stat alert">
                        <h5>Productos con Stock Bajo</h5>
                        <p class="stat-number">${reporte.productosBajoStock || 0}</p>
                    </div>
                </div>
            </div>
            
            ${reporte.alertasBajoStock && reporte.alertasBajoStock.length > 0 ? `
                <div class="report-section">
                    <h4>Alertas de Stock Bajo</h4>
                    <div class="table-container">
                        <table class="report-table">
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Producto</th>
                                    <th>Stock Actual</th>
                                    <th>Precio</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${reporte.alertasBajoStock.map(producto => `
                                    <tr>
                                        <td>${producto.codigo}</td>
                                        <td>${producto.nombre}</td>
                                        <td class="text-danger"><strong>${producto.stockActual}</strong></td>
                                        <td>€${producto.precioVentaSugerido.toFixed(2)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            ` : ''}
        `;
    } else if (tipo === 'top-productos') {
        // Mostrar productos más vendidos
        const periodo = reporte.fechaInicio && reporte.fechaFin 
            ? `${formatDate(reporte.fechaInicio)} - ${formatDate(reporte.fechaFin)}`
            : 'Último mes';
            
        let productosVendidos = [];
        if (reporte.ventasPorProducto) {
            // Convertir el mapa a un array ordenado por cantidad vendida
            productosVendidos = Object.entries(reporte.ventasPorProducto)
                .map(([producto, cantidad]) => ({
                    producto: JSON.parse(producto), // Si viene como string JSON
                    cantidad: cantidad
                }))
                .sort((a, b) => b.cantidad - a.cantidad)
                .slice(0, 10);
        }
        
        contenidoEspecifico = `
            <div class="report-section">
                <h4>Estadísticas del Período</h4>
                <div class="stats-grid">
                    <div class="report-stat success">
                        <h5>Total de Ventas</h5>
                        <p class="stat-number">${reporte.totalVentas || 0}</p>
                    </div>
                    <div class="report-stat">
                        <h5>Período Analizado</h5>
                        <p class="stat-number" style="font-size: 1rem;">${periodo}</p>
                    </div>
                </div>
            </div>
            
            ${productosVendidos.length > 0 ? `
                <div class="report-section">
                    <h4>Top 10 Productos Más Vendidos</h4>
                    <div class="table-container">
                        <table class="report-table">
                            <thead>
                                <tr>
                                    <th>Ranking</th>
                                    <th>Código</th>
                                    <th>Producto</th>
                                    <th>Unidades Vendidas</th>
                                    <th>Ingresos Generados</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${productosVendidos.map((item, index) => `
                                    <tr>
                                        <td>
                                            <strong style="color: ${index < 3 ? '#f59e0b' : '#6b7280'}">
                                                #${index + 1}
                                                ${index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' : ''}
                                            </strong>
                                        </td>
                                        <td>${item.producto?.codigo || 'N/A'}</td>
                                        <td>${item.producto?.nombre || 'Producto no disponible'}</td>
                                        <td class="text-success"><strong>${item.cantidad}</strong></td>
                                        <td>€${((reporte.ingresosPorProducto && reporte.ingresosPorProducto[JSON.stringify(item.producto)]) || 0).toFixed(2)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            ` : `
                <div class="report-section">
                    <div class="alert alert-info">
                        <h4>📊 Sin datos de ventas</h4>
                        <p>No se encontraron ventas en el período analizado. Los productos más vendidos se mostrarán cuando haya datos de ventas disponibles.</p>
                    </div>
                </div>
            `}
        `;
    } else {
        // Mostrar reporte de ventas
        const periodo = reporte.fechaInicio && reporte.fechaFin 
            ? `${formatDate(reporte.fechaInicio)} - ${formatDate(reporte.fechaFin)}`
            : 'Período actual';
            
        contenidoEspecifico = `
            <div class="report-section">
                <h4>Estadísticas de Ventas</h4>
                <div class="stats-grid">
                    <div class="report-stat">
                        <h5>Total de Ventas</h5>
                        <p class="stat-number">${reporte.totalVentas || 0}</p>
                    </div>
                    <div class="report-stat success">
                        <h5>Monto Total</h5>
                        <p class="stat-number">€${(reporte.montoTotal || 0).toFixed(2)}</p>
                    </div>
                </div>
                <p><strong>Período:</strong> ${periodo}</p>
            </div>
            
            ${reporte.ventas && reporte.ventas.length > 0 ? `
                <div class="report-section">
                    <h4>Detalle de Ventas (Últimas 10)</h4>
                    <div class="table-container">
                        <table class="report-table">
                            <thead>
                                <tr>
                                    <th>Factura</th>
                                    <th>Fecha</th>
                                    <th>Cliente</th>
                                    <th>Cantidad</th>
                                    <th>Total (EUR)</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${reporte.ventas.slice(0, 10).map(venta => `
                                    <tr>
                                        <td>${venta.numeroFactura}</td>
                                        <td>${formatDate(venta.fechaVenta)}</td>
                                        <td>${venta.cliente || 'No especificado'}</td>
                                        <td>${venta.cantidad}</td>
                                        <td>€${(venta.totalVentaEUR || 0).toFixed(2)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    </div>
                </div>
            ` : ''}
        `;
    }
    
    resultsContainer.innerHTML = `
        <div class="report-header">
            <h3><i class="fas fa-chart-bar"></i> ${title}</h3>
            <p class="report-date">Generado el ${formatDateTime(new Date())}</p>
        </div>
        <div class="report-content">
            ${contenidoEspecifico}
            
            <div class="report-actions">
                <button class="btn btn-primary" onclick="exportarReporteCSV('${title}', '${tipo}')">
                    <i class="fas fa-download"></i>
                    Exportar CSV
                </button>
                <button class="btn btn-outline" onclick="printReporte()">
                    <i class="fas fa-print"></i>
                    Imprimir
                </button>
                <button class="btn btn-success" onclick="generarReportePDF('${title}', '${tipo}')">
                    <i class="fas fa-file-pdf"></i>
                    Generar PDF
                </button>
            </div>
        </div>
    `;
    
    resultsContainer.classList.add('show');
    resultsContainer.scrollIntoView({ behavior: 'smooth' });
}

// ===== MODALES =====
function showAddProductModal() {
    showModal('product-modal');
}

async function showAddSaleModal() {
    try {
        // Cargar productos disponibles para el select
        const productos = await fetchAPI('/productos');
        const select = document.getElementById('productoId');
        
        // Limpiar opciones existentes (excepto la primera)
        select.innerHTML = '<option value="">Seleccionar producto</option>';
        
        // Agregar productos disponibles
        productos.forEach(producto => {
            if (producto.stockActual > 0) {
                const option = document.createElement('option');
                option.value = producto.id;
                option.textContent = `${producto.codigo} - ${producto.nombre} (Stock: ${producto.stockActual})`;
                option.dataset.precio = producto.precioVentaSugerido;
                select.appendChild(option);
            }
        });
        
        // Establecer fecha actual por defecto
        const now = new Date();
        const localDateTime = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
        document.getElementById('fechaVenta').value = localDateTime;
        
        // Generar número de factura automático
        const numeroFactura = generateInvoiceNumber();
        document.getElementById('numeroFactura').value = numeroFactura;
        
        showModal('sale-modal');
        
    } catch (error) {
        console.error('Error cargando productos:', error);
        showToast('Error cargando productos para la venta', 'error');
    }
}

function showModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.classList.remove('show');
    document.body.style.overflow = 'auto';
    
    // Limpiar formularios
    const form = modal.querySelector('form');
    if (form) form.reset();
}

function closeAllModals() {
    document.querySelectorAll('.modal').forEach(modal => {
        closeModal(modal.id);
    });
}

async function saveProduct() {
    const form = document.getElementById('product-form');
    const formData = new FormData(form);
    const editingId = form.dataset.editingId;
    
    const producto = {
        codigo: formData.get('codigo'),
        nombre: formData.get('nombre'),
        categoria: formData.get('categoria'),
        proveedor: formData.get('proveedor'),
        stockInicial: parseInt(formData.get('stockInicial')),
        costoImportacion: parseFloat(formData.get('costoImportacion')),
        precioVentaSugerido: parseFloat(formData.get('precioVentaSugerido')),
        monedaOrigen: formData.get('monedaOrigen'),
        descripcionTecnica: formData.get('descripcionTecnica')
    };
    
    // Si no estamos editando, agregar campos adicionales
    if (!editingId) {
        producto.stockActual = producto.stockInicial;
        producto.fechaImportacion = new Date().toISOString().split('T')[0];
    }
    
    try {
        showLoading(true);
        
        if (editingId) {
            // Actualizar producto existente
            await fetchAPI(`/productos/${editingId}`, 'PUT', producto);
            showToast('Producto actualizado exitosamente', 'success');
        } else {
            // Crear nuevo producto
            await fetchAPI('/productos', 'POST', producto);
            showToast('Producto creado exitosamente', 'success');
        }
        
        closeModal('product-modal');
        
        // Limpiar el formulario y resetear el estado de edición
        form.reset();
        delete form.dataset.editingId;
        
        // Resetear el modal a su estado original
        document.querySelector('#product-modal .modal-header h3').textContent = '📦 Nuevo Producto';
        document.querySelector('#product-modal .btn-primary').textContent = '💾 Guardar Producto';
        
        if (currentSection === 'productos') {
            loadProductos();
        }
        updateStats();
        
    } catch (error) {
        console.error('Error guardando producto:', error);
        showToast('Error guardando el producto', 'error');
    } finally {
        showLoading(false);
    }
}

async function saveSale() {
    try {
        const form = document.getElementById('sale-form');
        const formData = new FormData(form);
        
        // Validar campos requeridos
        if (!formData.get('numeroFactura') || !formData.get('fechaVenta') || !formData.get('productoId') || !formData.get('cantidad') || !formData.get('precioUnitario')) {
            showToast('Por favor, complete todos los campos requeridos', 'error');
            return;
        }
        
        const venta = {
            numeroFactura: formData.get('numeroFactura'),
            fechaVenta: formData.get('fechaVenta'),
            productoId: parseInt(formData.get('productoId')),
            cantidad: parseInt(formData.get('cantidad')),
            precioUnitario: parseFloat(formData.get('precioUnitario')),
            monedaLocal: formData.get('monedaLocal'),
            cliente: formData.get('cliente'),
            vendedorResponsable: formData.get('vendedorResponsable'),
            paisFilial: formData.get('paisFilial'),
            metodoPago: formData.get('metodoPago')
        };
        
        showLoading(true);
        await fetchAPI('/ventas', 'POST', venta);
        closeModal('sale-modal');
        showToast('Venta registrada exitosamente', 'success');
        
        if (currentSection === 'ventas') {
            loadVentas();
        }
        updateStats();
        
    } catch (error) {
        console.error('Error registrando venta:', error);
        showToast('Error al registrar venta', 'error');
    } finally {
        showLoading(false);
    }
}

// ===== HELPERS PARA MODALES =====
function generateInvoiceNumber() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    return `FAC-${year}${month}${day}${hours}${minutes}`;
}

function onProductSelectionChange() {
    const select = document.getElementById('productoId');
    const precioInput = document.getElementById('precioUnitario');
    
    if (select.selectedIndex > 0) {
        const selectedOption = select.options[select.selectedIndex];
        const precioSugerido = selectedOption.dataset.precio;
        if (precioSugerido) {
            precioInput.value = precioSugerido;
        }
    } else {
        precioInput.value = '';
    }
}

// ===== API HELPERS =====
async function fetchAPI(endpoint, method = 'GET', data = null) {
    const url = API_BASE_URL + endpoint;
    
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    if (data) {
        options.body = JSON.stringify(data);
    }
    
    const response = await fetch(url, options);
    
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return await response.json();
}

// ===== UI HELPERS =====
function showLoading(show) {
    const loading = document.getElementById('loading');
    if (show) {
        loading.classList.add('show');
    } else {
        loading.classList.remove('show');
    }
}

function showToast(message, type = 'info', duration = 5000) {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <div class="toast-content">
            <p>${message}</p>
        </div>
    `;
    
    container.appendChild(toast);
    
    // Auto remover después del tiempo especificado
    setTimeout(() => {
        toast.remove();
    }, duration);
    
    // Permitir cerrar manualmente
    toast.addEventListener('click', () => {
        toast.remove();
    });
}

// ===== UTILITY FUNCTIONS =====
function formatDate(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleDateString('es-ES');
}

function formatDateTime(date) {
    if (!date) return '';
    const d = new Date(date);
    return d.toLocaleString('es-ES');
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

async function exportarReporteCSV(title, tipo) {
    try {
        showLoading(true);
        let data = [];
        let filename = 'reporte_serf';
        
        // Obtener datos según el tipo de reporte
        switch(tipo) {
            case 'stock':
                data = await fetchAPI('/productos');
                filename = 'inventario_serf';
                break;
            case 'top-productos':
                data = await fetchAPI('/productos');
                filename = 'top_productos_serf';
                break;
            default:
                // Para reportes de ventas
                data = await fetchAPI('/ventas');
                filename = 'ventas_serf';
                break;
        }
        
        if (data.length === 0) {
            showToast('No hay datos para exportar', 'warning');
            return;
        }
        
        // Convertir a CSV
        const csv = convertToCSV(data);
        downloadCSV(csv, `${filename}_${new Date().toISOString().split('T')[0]}.csv`);
        showToast('Reporte exportado exitosamente', 'success');
        
    } catch (error) {
        console.error('Error exportando reporte:', error);
        showToast('Error al exportar reporte', 'error');
    } finally {
        showLoading(false);
    }
}

async function generarReportePDF(title, tipo) {
    try {
        showLoading(true);
        
        // Crear ventana nueva para generar el PDF
        const printWindow = window.open('', '_blank');
        const currentDate = new Date().toLocaleDateString('es-ES');
        
        let data = {};
        switch(tipo) {
            case 'stock':
                data = await fetchAPI('/reportes/productos/stock');
                break;
            case 'top-productos':
                data = await fetchAPI('/reportes/productos/stock');
                break;
            default:
                const fechaActual = new Date().toISOString().split('T')[0];
                data = await fetchAPI(`/reportes/ventas/mensual?fecha=${fechaActual}`);
                break;
        }
        
        let pdfContent = `
            <html>
            <head>
                <title>${title} - SERF</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        margin: 0; 
                        padding: 20px; 
                        background-color: #f8f9fa; 
                    }
                    .container { 
                        max-width: 800px; 
                        margin: 0 auto; 
                        background: white; 
                        padding: 30px; 
                        border-radius: 8px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header { 
                        text-align: center; 
                        margin-bottom: 30px; 
                        padding-bottom: 20px;
                        border-bottom: 3px solid #2c3e50; 
                    }
                    .header h1 { 
                        color: #2c3e50; 
                        margin: 0; 
                        font-size: 2.5em;
                        font-weight: 300;
                    }
                    .header .subtitle {
                        color: #7f8c8d;
                        font-size: 1.2em;
                        margin: 10px 0;
                    }
                    .stats-grid { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); 
                        gap: 20px; 
                        margin: 30px 0; 
                    }
                    .stat-card { 
                        padding: 20px; 
                        border: 2px solid #ecf0f1; 
                        border-radius: 8px; 
                        text-align: center;
                        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
                    }
                    .stat-card h3 { 
                        margin: 0 0 10px 0; 
                        color: #2c3e50; 
                        font-size: 1.8em;
                        font-weight: bold;
                    }
                    .stat-card p { 
                        margin: 0; 
                        color: #7f8c8d; 
                        font-weight: 500;
                    }
                    table { 
                        width: 100%; 
                        border-collapse: collapse; 
                        margin: 20px 0; 
                        background: white;
                    }
                    th, td { 
                        border: 1px solid #bdc3c7; 
                        padding: 12px 8px; 
                        text-align: left; 
                        font-size: 0.9em;
                    }
                    th { 
                        background: linear-gradient(135deg, #3498db 0%, #2980b9 100%); 
                        color: white; 
                        font-weight: 600;
                        text-transform: uppercase;
                        font-size: 0.8em;
                        letter-spacing: 0.5px;
                    }
                    .footer { 
                        margin-top: 40px; 
                        padding-top: 20px;
                        border-top: 2px solid #ecf0f1;
                        text-align: center; 
                        font-size: 0.9em; 
                        color: #7f8c8d; 
                    }
                    .company-info {
                        font-size: 1.1em;
                        color: #2c3e50;
                        font-weight: 500;
                    }
                    .danger { color: #e74c3c; font-weight: bold; }
                    .success { color: #27ae60; font-weight: bold; }
                    .section-title {
                        font-size: 1.4em;
                        color: #2c3e50;
                        margin: 30px 0 15px 0;
                        padding-bottom: 10px;
                        border-bottom: 2px solid #ecf0f1;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📊 ${title}</h1>
                        <p class="subtitle company-info">Sistema Empresarial de Gestión de Reportes Financieros (SERF)</p>
                        <p class="company-info"><strong>FinanCorp S.A.</strong></p>
                        <p>Fecha de generación: ${currentDate}</p>
                    </div>
        `;
        
        // Agregar contenido específico según el tipo
        if (tipo === 'stock') {
            pdfContent += `
                    <div class="stats-grid">
                        <div class="stat-card">
                            <h3>${data.totalProductos || 0}</h3>
                            <p>Total de Productos</p>
                        </div>
                        <div class="stat-card">
                            <h3 class="danger">${data.productosBajoStock || 0}</h3>
                            <p>Productos con Stock Bajo</p>
                        </div>
                    </div>
                    
                    ${data.alertasBajoStock && data.alertasBajoStock.length > 0 ? `
                        <h2 class="section-title">⚠️ Alertas de Stock Bajo</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Producto</th>
                                    <th>Stock Actual</th>
                                    <th>Precio (EUR)</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${data.alertasBajoStock.map(producto => `
                                    <tr>
                                        <td>${producto.codigo}</td>
                                        <td>${producto.nombre}</td>
                                        <td class="danger">${producto.stockActual}</td>
                                        <td>€${producto.precioVentaSugerido.toFixed(2)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    ` : ''}
            `;
        } else {
            // Reporte de ventas
            pdfContent += `
                    <div class="stats-grid">
                        <div class="stat-card">
                            <h3>${data.totalVentas || 0}</h3>
                            <p>Total de Ventas</p>
                        </div>
                        <div class="stat-card">
                            <h3 class="success">€${(data.montoTotal || 0).toFixed(2)}</h3>
                            <p>Monto Total</p>
                        </div>
                    </div>
                    
                    ${data.ventas && data.ventas.length > 0 ? `
                        <h2 class="section-title">💼 Detalle de Ventas</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>Factura</th>
                                    <th>Fecha</th>
                                    <th>Cliente</th>
                                    <th>Cantidad</th>
                                    <th>Total (EUR)</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${data.ventas.slice(0, 15).map(venta => `
                                    <tr>
                                        <td>${venta.numeroFactura}</td>
                                        <td>${new Date(venta.fechaVenta).toLocaleDateString('es-ES')}</td>
                                        <td>${venta.cliente || 'No especificado'}</td>
                                        <td>${venta.cantidad}</td>
                                        <td class="success">€${(venta.totalVentaEUR || 0).toFixed(2)}</td>
                                    </tr>
                                `).join('')}
                            </tbody>
                        </table>
                    ` : ''}
            `;
        }
        
        pdfContent += `
                    <div class="footer">
                        <p><strong>Generado por SERF</strong> - Sistema Empresarial de Gestión de Reportes Financieros</p>
                        <p>© ${new Date().getFullYear()} FinanCorp S.A. - Todos los derechos reservados</p>
                        <p><em>Documento generado automáticamente el ${new Date().toLocaleString('es-ES')}</em></p>
                    </div>
                </div>
            </body>
            </html>
        `;
        
        printWindow.document.write(pdfContent);
        printWindow.document.close();
        
        // Esperar a que cargue y luego mostrar diálogo de impresión
        setTimeout(() => {
            printWindow.print();
        }, 1000);
        
        showToast('Reporte PDF generado. Use Ctrl+P para imprimir o guardar como PDF.', 'success');
        
    } catch (error) {
        console.error('Error generando PDF:', error);
        showToast('Error al generar PDF', 'error');
    } finally {
        showLoading(false);
    }
}

function printReporte() {
    try {
        // Crear ventana de impresión con el contenido actual
        const printWindow = window.open('', '_blank');
        const currentDate = new Date().toLocaleDateString('es-PE');
        
        let printContent = `
            <html>
            <head>
                <title>Reporte SERF - ${currentDate}</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    h1 { color: #2c3e50; text-align: center; }
                    .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    .stats { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-bottom: 30px; }
                    .stat-card { padding: 15px; border: 1px solid #ddd; border-radius: 8px; }
                    .stat-card h3 { margin: 0; color: #2c3e50; }
                    .stat-card p { margin: 5px 0; font-size: 18px; font-weight: bold; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    th { background-color: #3498db; color: white; }
                    .footer { margin-top: 30px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="header">
                    <h1>Sistema Empresarial de Gestión de Reportes Financieros (SERF)</h1>
                    <p><strong>FinanCorp S.A.</strong></p>
                    <p>Fecha: ${currentDate}</p>
                </div>
        `;
        
        // Agregar contenido según la sección actual
        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            printContent += mainContent.outerHTML;
        }
        
        printContent += `
                <div class="footer">
                    <p>Generado por SERF - Sistema Empresarial de Gestión de Reportes Financieros</p>
                    <p>© ${new Date().getFullYear()} FinanCorp S.A. - Todos los derechos reservados</p>
                </div>
            </body>
            </html>
        `;
        
        printWindow.document.write(printContent);
        printWindow.document.close();
        
        // Esperar a que cargue y luego imprimir
        setTimeout(() => {
            printWindow.print();
            printWindow.close();
        }, 500);
        
        showToast('Reporte enviado a impresión', 'success');
        
    } catch (error) {
        console.error('Error imprimiendo reporte:', error);
        showToast('Error al imprimir reporte', 'error');
    }
}

async function editProduct(id) {
    try {
        showLoading(true);
        const producto = await fetchAPI(`/productos/${id}`);
        
        if (!producto) {
            showToast('Producto no encontrado', 'error');
            return;
        }
        
        // Llenar el formulario con los datos del producto
        document.getElementById('codigo').value = producto.codigo;
        document.getElementById('nombre').value = producto.nombre;
        document.getElementById('categoria').value = producto.categoria;
        document.getElementById('proveedor').value = producto.proveedor || '';
        document.getElementById('stockInicial').value = producto.stockInicial;
        document.getElementById('costoImportacion').value = producto.costoImportacion;
        document.getElementById('precioVentaSugerido').value = producto.precioVentaSugerido;
        document.getElementById('monedaOrigen').value = producto.monedaOrigen;
        document.getElementById('descripcionTecnica').value = producto.descripcionTecnica || '';
        
        // Cambiar el título del modal
        document.querySelector('#product-modal .modal-header h3').textContent = '✏️ Editar Producto';
        
        // Cambiar el botón de guardar
        const saveButton = document.querySelector('#product-modal .btn-primary');
        saveButton.textContent = '💾 Actualizar Producto';
        
        // Guardar el ID para la actualización
        document.getElementById('product-form').dataset.editingId = id;
        
        showModal('product-modal');
        
    } catch (error) {
        console.error('Error cargando producto:', error);
        showToast('Error cargando datos del producto', 'error');
    } finally {
        showLoading(false);
    }
}

async function deleteProduct(id) {
    if (confirm('¿Está seguro de que desea eliminar este producto? Esta acción no se puede deshacer.')) {
        try {
            showLoading(true);
            await fetchAPI(`/productos/${id}`, 'DELETE');
            showToast('Producto eliminado exitosamente', 'success');
            
            if (currentSection === 'productos') {
                loadProductos();
            }
            updateStats();
            
        } catch (error) {
            console.error('Error eliminando producto:', error);
            showToast('Error al eliminar producto', 'error');
        } finally {
            showLoading(false);
        }
    }
}

// ===== FUNCIONES AUXILIARES PARA EXPORTACIÓN =====
function convertToCSV(data) {
    if (!data || data.length === 0) return '';
    
    const headers = Object.keys(data[0]);
    const csvContent = [
        headers.join(','),
        ...data.map(row => 
            headers.map(header => {
                const value = row[header];
                // Escapar comillas y envolver en comillas si contiene comas
                const stringValue = String(value || '');
                return stringValue.includes(',') ? `"${stringValue.replace(/"/g, '""')}"` : stringValue;
            }).join(',')
        )
    ].join('\n');
    
    return csvContent;
}

function downloadCSV(csvContent, filename) {
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    
    if (navigator.msSaveBlob) {
        // IE 10+
        navigator.msSaveBlob(blob, filename);
    } else {
        // Otros navegadores
        const url = URL.createObjectURL(blob);
        link.href = url;
        link.download = filename;
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    }
}

// ===== FUNCIONES ADICIONALES PARA ESTADÍSTICAS =====
async function updateStats() {
    try {
        if (currentSection === 'dashboard') {
            loadDashboardData();
        }
    } catch (error) {
        console.error('Error actualizando estadísticas:', error);
    }
}

// ===== CONFIGURACIÓN ADICIONAL =====

// Agregar estilos CSS adicionales dinámicamente si es necesario
const additionalStyles = `
    .report-content {
        max-height: 600px;
        overflow-y: auto;
    }
    
    .report-header {
        margin-bottom: 2rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #e9ecef;
    }
    
    .report-header h3 {
        margin: 0;
        color: #2c3e50;
        font-size: 1.75rem;
        font-weight: 600;
    }
    
    .report-date {
        margin: 0.5rem 0 0 0;
        color: #6c757d;
        font-size: 0.9rem;
    }
    
    .report-section {
        margin-bottom: 2rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #e9ecef;
    }
    
    .report-section:last-child {
        border-bottom: none;
    }
    
    .report-section h4 {
        font-size: 1.25rem;
        font-weight: 600;
        margin-bottom: 1rem;
        color: #495057;
    }
    
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
        gap: 1rem;
        margin-bottom: 1.5rem;
    }
    
    .report-stat {
        background: #f8f9fa;
        border: 2px solid #e9ecef;
        border-radius: 8px;
        padding: 1.5rem;
        text-align: center;
        transition: all 0.3s ease;
    }
    
    .report-stat:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
    
    .report-stat.success {
        border-color: #28a745;
        background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
    }
    
    .report-stat.alert {
        border-color: #dc3545;
        background: linear-gradient(135deg, #f8d7da 0%, #f5c6cb 100%);
    }
    
    .report-stat h5 {
        margin: 0 0 0.5rem 0;
        color: #495057;
        font-size: 0.9rem;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
    
    .stat-number {
        margin: 0;
        font-size: 2rem;
        font-weight: bold;
        color: #2c3e50;
    }
    
    .report-table {
        width: 100%;
        border-collapse: collapse;
        margin: 1rem 0;
        background: white;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    
    .report-table th {
        background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
        color: white;
        padding: 1rem 0.75rem;
        font-weight: 600;
        text-transform: uppercase;
        font-size: 0.8rem;
        letter-spacing: 0.5px;
    }
    
    .report-table td {
        padding: 0.75rem;
        border-bottom: 1px solid #e9ecef;
    }
    
    .report-table tbody tr:hover {
        background-color: #f8f9fa;
    }
    
    .text-danger {
        color: #dc3545 !important;
        font-weight: bold;
    }
    
    .text-success {
        color: #28a745 !important;
        font-weight: bold;
    }
    
    .report-actions {
        display: flex;
        gap: 0.75rem;
        justify-content: flex-end;
        margin-top: 2rem;
        padding-top: 1rem;
        border-top: 2px solid #e9ecef;
        flex-wrap: wrap;
    }
    
    .report-actions .btn {
        min-width: 140px;
    }
    
    .btn-sm {
        padding: 0.5rem 0.75rem;
        font-size: 0.8rem;
    }
    
    .toast-content p {
        margin: 0;
        font-size: 0.9rem;
        font-weight: 500;
    }
    
    #report-results {
        display: none;
        margin-top: 2rem;
        padding: 2rem;
        background: white;
        border-radius: 12px;
        box-shadow: 0 4px 16px rgba(0,0,0,0.1);
        animation: slideIn 0.3s ease-out;
    }
    
    #report-results.show {
        display: block;
    }
    
    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateY(-20px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    .table-container {
        overflow-x: auto;
        margin: 1rem 0;
    }
    
    @media (max-width: 768px) {
        .stats-grid {
            grid-template-columns: 1fr;
        }
        
        .report-actions {
            justify-content: center;
        }
        
        .report-actions .btn {
            min-width: auto;
            flex: 1;
        }
    }
`;

// Inyectar estilos adicionales
const styleSheet = document.createElement('style');
styleSheet.textContent = additionalStyles;
document.head.appendChild(styleSheet);

// ===== FUNCIONES DE AUTENTICACIÓN =====
function toggleUserDropdown() {
    const dropdown = document.getElementById('user-dropdown');
    const userMenu = document.querySelector('.user-menu');
    
    if (dropdown.classList.contains('show')) {
        closeUserDropdown();
    } else {
        openUserDropdown();
    }
}

function openUserDropdown() {
    const dropdown = document.getElementById('user-dropdown');
    const userMenu = document.querySelector('.user-menu');
    
    dropdown.classList.add('show');
    userMenu.classList.add('active');
    
    // Cerrar cuando se haga click fuera
    setTimeout(() => {
        document.addEventListener('click', handleOutsideClick);
    }, 100);
}

function closeUserDropdown() {
    const dropdown = document.getElementById('user-dropdown');
    const userMenu = document.querySelector('.user-menu');
    
    dropdown.classList.remove('show');
    userMenu.classList.remove('active');
    
    document.removeEventListener('click', handleOutsideClick);
}

function handleOutsideClick(event) {
    const userMenu = document.querySelector('.user-menu');
    
    if (!userMenu.contains(event.target)) {
        closeUserDropdown();
    }
}

// Cerrar dropdown con ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        closeUserDropdown();
    }
});

console.log('🚀 SERF Sistema - Aplicación inicializada correctamente');