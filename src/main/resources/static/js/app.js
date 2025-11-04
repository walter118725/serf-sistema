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
        
        switch(tipo) {
            case 'mensual':
                endpoint = '/reportes/mensual';
                title = 'Reporte Mensual';
                break;
            case 'trimestral':
                endpoint = '/reportes/trimestral';
                title = 'Reporte Trimestral';
                break;
            case 'anual':
                endpoint = '/reportes/anual';
                title = 'Reporte Anual';
                break;
            case 'stock':
                endpoint = '/reportes/stock';
                title = 'Reporte de Inventarios';
                break;
        }
        
        const reporte = await fetchAPI(endpoint);
        displayReporte(reporte, title);
        
    } catch (error) {
        console.error('Error generando reporte:', error);
        showToast('Error generando el reporte', 'error');
    } finally {
        showLoading(false);
    }
}

function displayReporte(reporte, title) {
    const resultsContainer = document.getElementById('report-results');
    
    resultsContainer.innerHTML = `
        <h3>${title}</h3>
        <div class="report-content">
            <div class="report-section">
                <h4>Resumen</h4>
                <p><strong>Período:</strong> ${reporte.periodo || 'No especificado'}</p>
                <p><strong>Total de Componentes:</strong> ${reporte.componentes?.length || 0}</p>
                <p><strong>Fecha de Generación:</strong> ${formatDateTime(new Date())}</p>
            </div>
            
            ${reporte.componentes && reporte.componentes.length > 0 ? `
                <div class="report-section">
                    <h4>Detalles</h4>
                    <div class="report-items">
                        ${reporte.componentes.map(componente => `
                            <div class="report-item">
                                <p><strong>${componente.titulo || 'Item'}:</strong> ${componente.contenido || 'Sin contenido'}</p>
                            </div>
                        `).join('')}
                    </div>
                </div>
            ` : ''}
            
            <div class="report-actions mt-2">
                <button class="btn btn-primary" onclick="exportReporte('${title}')">
                    <i class="fas fa-download"></i>
                    Exportar PDF
                </button>
                <button class="btn btn-outline" onclick="printReporte()">
                    <i class="fas fa-print"></i>
                    Imprimir
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

async function exportReporte(title) {
    try {
        let data = [];
        let filename = 'reporte_serf';
        
        if (currentSection === 'productos') {
            data = await fetchAPI('/productos');
            filename = 'productos_serf';
        } else if (currentSection === 'ventas') {
            data = await fetchAPI('/ventas');
            filename = 'ventas_serf';
        } else if (currentSection === 'reportes') {
            data = await fetchAPI('/reportes');
            filename = 'reportes_serf';
        } else {
            // Exportar dashboard con estadísticas
            const stats = await fetchAPI('/ventas/estadisticas');
            data = [stats];
            filename = 'dashboard_serf';
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

// ===== CONFIGURACIÓN ADICIONAL =====

// Agregar estilos CSS adicionales dinámicamente si es necesario
const additionalStyles = `
    .report-content {
        max-height: 400px;
        overflow-y: auto;
    }
    
    .report-section {
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid var(--gray-200);
    }
    
    .report-section:last-child {
        border-bottom: none;
    }
    
    .report-section h4 {
        font-size: var(--font-size-lg);
        font-weight: 600;
        margin-bottom: 0.75rem;
        color: var(--gray-900);
    }
    
    .report-item {
        background: var(--gray-50);
        padding: 0.75rem;
        border-radius: var(--border-radius);
        margin-bottom: 0.5rem;
    }
    
    .report-actions {
        display: flex;
        gap: 1rem;
        justify-content: flex-end;
        margin-top: 1rem;
        padding-top: 1rem;
        border-top: 1px solid var(--gray-200);
    }
    
    .btn-sm {
        padding: 0.5rem 0.75rem;
        font-size: var(--font-size-xs);
    }
    
    .toast-content p {
        margin: 0;
        font-size: var(--font-size-sm);
        font-weight: 500;
    }
`;

// Inyectar estilos adicionales
const styleSheet = document.createElement('style');
styleSheet.textContent = additionalStyles;
document.head.appendChild(styleSheet);

console.log('🚀 SERF Sistema - Aplicación inicializada correctamente');