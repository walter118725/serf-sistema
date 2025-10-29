// Inventario JavaScript - SERF Sistema
const API_BASE = '/api';

let allInventory = [];
let allProducts = [];
let currentEditId = null;

// Cargar datos al iniciar
document.addEventListener('DOMContentLoaded', function() {
    loadInventory();
    loadProductsForSelect();
    loadAlerts();

    // Event listeners
    document.getElementById('searchInput').addEventListener('input', filterInventory);
    document.getElementById('countryFilter').addEventListener('change', filterInventory);
    document.getElementById('stockStatusFilter').addEventListener('change', filterInventory);
});

// Cargar todo el inventario
async function loadInventory() {
    try {
        const response = await fetch(`${API_BASE}/inventario`);
        allInventory = await response.json();
        displayInventory(allInventory);
        updateInventoryStats(allInventory);
    } catch (error) {
        console.error('Error cargando inventario:', error);
        showErrorMessage('Error al cargar el inventario');
    }
}

// Mostrar inventario en la tabla
function displayInventory(inventory) {
    const content = document.getElementById('inventoryContent');
    document.getElementById('inventoryCount').textContent = `${inventory.length} registros`;

    if (inventory.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                No se encontraron registros de inventario.
            </div>
        `;
        return;
    }

    let html = '<div class="table-container"><table class="table">';
    html += `
        <thead>
            <tr>
                <th>ID</th>
                <th>Producto</th>
                <th>Ubicación</th>
                <th>País</th>
                <th>Stock Actual</th>
                <th>Stock Mínimo</th>
                <th>Estado</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
    `;

    inventory.forEach(item => {
        const isLowStock = item.cantidad < item.stockMinimo;
        const stockPercentage = (item.cantidad / item.stockMinimo) * 100;

        let statusBadge = '';
        if (isLowStock) {
            statusBadge = '<span class="badge badge-danger"><i class="fas fa-exclamation-triangle"></i> Stock Bajo</span>';
        } else if (stockPercentage <= 150) {
            statusBadge = '<span class="badge badge-warning"><i class="fas fa-check"></i> Stock Normal</span>';
        } else {
            statusBadge = '<span class="badge badge-success"><i class="fas fa-check-circle"></i> Stock Óptimo</span>';
        }

        html += `
            <tr class="fade-in ${isLowStock ? 'text-danger' : ''}">
                <td>${item.id}</td>
                <td><strong>${item.producto.nombre}</strong></td>
                <td>${item.ubicacion}</td>
                <td><span class="badge badge-info">${item.pais}</span></td>
                <td><strong style="${isLowStock ? 'color: var(--danger-color);' : ''}">${item.cantidad}</strong></td>
                <td>${item.stockMinimo}</td>
                <td>${statusBadge}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon btn-icon-edit" onclick="editInventory(${item.id})" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-icon btn-icon-delete" onclick="deleteInventory(${item.id})" title="Eliminar">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });

    html += '</tbody></table></div>';
    content.innerHTML = html;
}

// Actualizar estadísticas de inventario
function updateInventoryStats(inventory) {
    // Total de items
    const totalItems = inventory.reduce((sum, item) => sum + item.cantidad, 0);
    document.getElementById('totalItems').textContent = totalItems;

    // Conteo de alertas (stock bajo)
    const lowStockCount = inventory.filter(item => item.cantidad < item.stockMinimo).length;
    document.getElementById('lowStockCount').textContent = lowStockCount;

    // Stock óptimo
    const goodStockCount = inventory.filter(item => item.cantidad >= item.stockMinimo).length;
    document.getElementById('goodStockCount').textContent = goodStockCount;

    // Ubicaciones únicas
    const uniqueLocations = new Set(inventory.map(item => item.ubicacion));
    document.getElementById('locationCount').textContent = uniqueLocations.size;
}

// Cargar alertas de stock bajo
async function loadAlerts() {
    try {
        const response = await fetch(`${API_BASE}/inventario/alertas`);
        const alertas = await response.json();

        const alertsContent = document.getElementById('alertsContent');

        if (alertas.length === 0) {
            alertsContent.innerHTML = `
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    No hay alertas de stock bajo. Todo el inventario está en niveles saludables.
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
                    <th>Déficit</th>
                    <th>Acción</th>
                </tr>
            </thead>
            <tbody>
        `;

        alertas.forEach(alerta => {
            const deficit = alerta.stockMinimo - alerta.cantidad;
            html += `
                <tr style="background-color: rgba(231, 76, 60, 0.05);">
                    <td><strong>${alerta.producto.nombre}</strong></td>
                    <td>${alerta.ubicacion}</td>
                    <td><span class="badge badge-info">${alerta.pais}</span></td>
                    <td><strong style="color: var(--danger-color);">${alerta.cantidad}</strong></td>
                    <td>${alerta.stockMinimo}</td>
                    <td>
                        <span class="badge badge-danger">
                            <i class="fas fa-arrow-down"></i>
                            ${deficit} unidades
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-warning btn-sm" onclick="editInventory(${alerta.id})">
                            <i class="fas fa-plus"></i>
                            Reabastecer
                        </button>
                    </td>
                </tr>
            `;
        });

        html += '</tbody></table></div>';
        alertsContent.innerHTML = html;

    } catch (error) {
        console.error('Error cargando alertas:', error);
        document.getElementById('alertsContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                Error al cargar las alertas.
            </div>
        `;
    }
}

// Actualizar alertas
function refreshAlerts() {
    loadAlerts();
    loadInventory();
}

// Cargar productos para el selector
async function loadProductsForSelect() {
    try {
        const response = await fetch(`${API_BASE}/productos`);
        allProducts = await response.json();

        const select = document.getElementById('inventoryProduct');
        select.innerHTML = '<option value="">Seleccionar producto...</option>';

        allProducts.forEach(producto => {
            const option = document.createElement('option');
            option.value = producto.id;
            option.textContent = `${producto.nombre} (${producto.categoria})`;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando productos:', error);
    }
}

// Filtrar inventario
function filterInventory() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const country = document.getElementById('countryFilter').value;
    const stockStatus = document.getElementById('stockStatusFilter').value;

    let filtered = allInventory.filter(item => {
        const matchesSearch = item.producto.nombre.toLowerCase().includes(searchTerm) ||
                            item.ubicacion.toLowerCase().includes(searchTerm);
        const matchesCountry = !country || item.pais === country;

        let matchesStatus = true;
        if (stockStatus === 'low') {
            matchesStatus = item.cantidad < item.stockMinimo;
        } else if (stockStatus === 'normal') {
            matchesStatus = item.cantidad >= item.stockMinimo;
        }

        return matchesSearch && matchesCountry && matchesStatus;
    });

    displayInventory(filtered);
    updateInventoryStats(filtered);
}

// Limpiar filtros
function clearInventoryFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('countryFilter').value = '';
    document.getElementById('stockStatusFilter').value = '';
    displayInventory(allInventory);
    updateInventoryStats(allInventory);
}

// Abrir modal para agregar inventario
function openAddInventoryModal() {
    currentEditId = null;
    document.getElementById('modalTitle').textContent = 'Agregar al Inventario';
    document.getElementById('inventoryForm').reset();
    document.getElementById('inventoryId').value = '';
    document.getElementById('inventoryModal').classList.add('active');
}

// Editar inventario
async function editInventory(id) {
    try {
        const response = await fetch(`${API_BASE}/inventario/${id}`);
        const item = await response.json();

        currentEditId = id;
        document.getElementById('modalTitle').textContent = 'Editar Inventario';
        document.getElementById('inventoryId').value = item.id;
        document.getElementById('inventoryProduct').value = item.producto.id;
        document.getElementById('inventoryQuantity').value = item.cantidad;
        document.getElementById('inventoryMinStock').value = item.stockMinimo;
        document.getElementById('inventoryLocation').value = item.ubicacion;
        document.getElementById('inventoryCountry').value = item.pais;

        document.getElementById('inventoryModal').classList.add('active');

    } catch (error) {
        console.error('Error cargando inventario:', error);
        alert('Error al cargar el registro de inventario');
    }
}

// Guardar inventario
async function saveInventory() {
    const form = document.getElementById('inventoryForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const inventario = {
        producto: { id: document.getElementById('inventoryProduct').value },
        cantidad: parseInt(document.getElementById('inventoryQuantity').value),
        stockMinimo: parseInt(document.getElementById('inventoryMinStock').value),
        ubicacion: document.getElementById('inventoryLocation').value,
        pais: document.getElementById('inventoryCountry').value
    };

    try {
        let response;
        if (currentEditId) {
            // Actualizar
            response = await fetch(`${API_BASE}/inventario/${currentEditId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(inventario)
            });
        } else {
            // Crear
            response = await fetch(`${API_BASE}/inventario`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(inventario)
            });
        }

        if (response.ok) {
            closeInventoryModal();
            loadInventory();
            loadAlerts();
            showSuccessMessage(currentEditId ? 'Inventario actualizado exitosamente' : 'Inventario agregado exitosamente');
        } else {
            throw new Error('Error al guardar el inventario');
        }

    } catch (error) {
        console.error('Error guardando inventario:', error);
        alert('Error al guardar el inventario');
    }
}

// Eliminar inventario
async function deleteInventory(id) {
    if (!confirm('¿Está seguro de que desea eliminar este registro de inventario?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/inventario/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadInventory();
            loadAlerts();
            showSuccessMessage('Registro eliminado exitosamente');
        } else {
            throw new Error('Error al eliminar el registro');
        }

    } catch (error) {
        console.error('Error eliminando inventario:', error);
        alert('Error al eliminar el registro de inventario');
    }
}

// Cerrar modal
function closeInventoryModal() {
    document.getElementById('inventoryModal').classList.remove('active');
    currentEditId = null;
}

// Cerrar modal al hacer clic fuera
document.getElementById('inventoryModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeInventoryModal();
    }
});

// Mensajes
function showSuccessMessage(message) {
    alert(message);
}

function showErrorMessage(message) {
    alert(message);
}
