// Ventas JavaScript - SERF Sistema
const API_BASE = '/api';

let allSales = [];
let allProducts = [];

// Mapeo de países a monedas
const countryToCurrency = {
    'PERU': 'PEN',
    'MEXICO': 'MXN',
    'ESPAÑA': 'EUR'
};

// Cargar datos al iniciar
document.addEventListener('DOMContentLoaded', function() {
    loadSales();
    loadProductsForSelect();

    // Establecer fecha actual por defecto
    document.getElementById('saleDate').value = new Date().toISOString().split('T')[0];

    // Event listeners
    document.getElementById('countryFilter').addEventListener('change', filterSales);
});

// Cargar todas las ventas
async function loadSales() {
    try {
        const response = await fetch(`${API_BASE}/ventas`);
        allSales = await response.json();
        displaySales(allSales);
        updateSalesStats(allSales);
    } catch (error) {
        console.error('Error cargando ventas:', error);
        showErrorMessage('Error al cargar las ventas');
    }
}

// Mostrar ventas en la tabla
function displaySales(sales) {
    const content = document.getElementById('salesContent');
    document.getElementById('salesCount').textContent = `${sales.length} ventas`;

    if (sales.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                No se encontraron ventas.
            </div>
        `;
        return;
    }

    // Ordenar por fecha (más recientes primero)
    const sortedSales = [...sales].sort((a, b) => new Date(b.fecha) - new Date(a.fecha));

    let html = '<div class="table-container"><table class="table">';
    html += `
        <thead>
            <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Producto</th>
                <th>Cantidad</th>
                <th>País</th>
                <th>Moneda</th>
                <th>Total</th>
            </tr>
        </thead>
        <tbody>
    `;

    sortedSales.forEach(venta => {
        const fecha = new Date(venta.fecha).toLocaleDateString('es-ES');
        html += `
            <tr class="fade-in">
                <td>${venta.id}</td>
                <td>${fecha}</td>
                <td><strong>${venta.producto.nombre}</strong></td>
                <td>${venta.cantidad} unidades</td>
                <td><span class="badge badge-info">${venta.pais}</span></td>
                <td>${venta.moneda}</td>
                <td><strong>${venta.moneda} ${venta.total.toFixed(2)}</strong></td>
            </tr>
        `;
    });

    html += '</tbody></table></div>';
    content.innerHTML = html;
}

// Actualizar estadísticas de ventas
function updateSalesStats(sales) {
    // Total de ventas (en USD para simplificar - en producción convertirías las monedas)
    const totalAmount = sales.reduce((sum, venta) => sum + venta.total, 0);
    document.getElementById('totalSalesAmount').textContent = `$${totalAmount.toFixed(2)}`;

    // Total de transacciones
    document.getElementById('totalTransactions').textContent = sales.length;

    // Promedio por venta
    const average = sales.length > 0 ? totalAmount / sales.length : 0;
    document.getElementById('averageSale').textContent = `$${average.toFixed(2)}`;

    // Total de unidades vendidas
    const totalUnits = sales.reduce((sum, venta) => sum + venta.cantidad, 0);
    document.getElementById('totalUnits').textContent = totalUnits;
}

// Cargar productos para el selector
async function loadProductsForSelect() {
    try {
        const response = await fetch(`${API_BASE}/productos`);
        allProducts = await response.json();

        const select = document.getElementById('saleProduct');
        select.innerHTML = '<option value="">Seleccionar producto...</option>';

        allProducts.forEach(producto => {
            const option = document.createElement('option');
            option.value = producto.id;
            option.textContent = `${producto.nombre} - $${producto.precio.toFixed(2)}`;
            option.dataset.precio = producto.precio;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando productos:', error);
    }
}

// Filtrar ventas
function filterSales() {
    const country = document.getElementById('countryFilter').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    let filtered = allSales.filter(venta => {
        const matchesCountry = !country || venta.pais === country;

        let matchesDate = true;
        if (startDate || endDate) {
            const ventaDate = new Date(venta.fecha);
            if (startDate) {
                matchesDate = matchesDate && ventaDate >= new Date(startDate);
            }
            if (endDate) {
                matchesDate = matchesDate && ventaDate <= new Date(endDate);
            }
        }

        return matchesCountry && matchesDate;
    });

    displaySales(filtered);
    updateSalesStats(filtered);
}

// Limpiar filtros
function clearSalesFilters() {
    document.getElementById('countryFilter').value = '';
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    displaySales(allSales);
    updateSalesStats(allSales);
}

// Abrir modal para registrar venta
function openAddSaleModal() {
    document.getElementById('saleForm').reset();
    document.getElementById('saleDate').value = new Date().toISOString().split('T')[0];
    document.getElementById('saleQuantity').value = 1;
    document.getElementById('saleModal').classList.add('active');
}

// Actualizar precio del producto seleccionado
function updateProductPrice() {
    const select = document.getElementById('saleProduct');
    const selectedOption = select.options[select.selectedIndex];

    if (selectedOption.dataset.precio) {
        document.getElementById('saleUnitPrice').value = parseFloat(selectedOption.dataset.precio).toFixed(2);
        calculateTotal();
    }
}

// Actualizar moneda según el país
function updateCurrency() {
    const country = document.getElementById('saleCountry').value;
    const currency = countryToCurrency[country] || '';
    document.getElementById('saleCurrency').value = currency;
    calculateTotal();
}

// Calcular total
function calculateTotal() {
    const quantity = parseInt(document.getElementById('saleQuantity').value) || 0;
    const unitPrice = parseFloat(document.getElementById('saleUnitPrice').value) || 0;
    const total = quantity * unitPrice;
    document.getElementById('saleTotal').value = total.toFixed(2);
}

// Guardar venta
async function saveSale() {
    const form = document.getElementById('saleForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const productoId = document.getElementById('saleProduct').value;
    const cantidad = parseInt(document.getElementById('saleQuantity').value);
    const pais = document.getElementById('saleCountry').value;
    const fecha = document.getElementById('saleDate').value;
    const moneda = document.getElementById('saleCurrency').value;
    const total = parseFloat(document.getElementById('saleTotal').value);

    const venta = {
        producto: { id: productoId },
        cantidad: cantidad,
        pais: pais,
        fecha: fecha,
        moneda: moneda,
        total: total
    };

    try {
        const response = await fetch(`${API_BASE}/ventas`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(venta)
        });

        if (response.ok) {
            closeSaleModal();
            loadSales();
            showSuccessMessage('Venta registrada exitosamente');
        } else {
            throw new Error('Error al registrar la venta');
        }

    } catch (error) {
        console.error('Error guardando venta:', error);
        alert('Error al registrar la venta');
    }
}

// Cerrar modal
function closeSaleModal() {
    document.getElementById('saleModal').classList.remove('active');
}

// Cerrar modal al hacer clic fuera
document.getElementById('saleModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeSaleModal();
    }
});

// Mensajes
function showSuccessMessage(message) {
    alert(message);
}

function showErrorMessage(message) {
    alert(message);
}
