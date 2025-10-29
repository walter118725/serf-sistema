// Productos JavaScript - SERF Sistema
const API_BASE = '/api';

let allProducts = [];
let currentEditId = null;

// Cargar datos al iniciar
document.addEventListener('DOMContentLoaded', function() {
    loadProducts();

    // Event listeners para filtros
    document.getElementById('searchInput').addEventListener('input', filterProducts);
    document.getElementById('categoryFilter').addEventListener('change', filterProducts);
    document.getElementById('countryFilter').addEventListener('change', filterProducts);
});

// Cargar todos los productos
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/productos`);
        allProducts = await response.json();
        displayProducts(allProducts);
    } catch (error) {
        console.error('Error cargando productos:', error);
        showErrorMessage('Error al cargar los productos');
    }
}

// Mostrar productos en la tabla
function displayProducts(products) {
    const content = document.getElementById('productosContent');
    document.getElementById('productCount').textContent = `${products.length} productos`;

    if (products.length === 0) {
        content.innerHTML = `
            <div class="alert alert-info">
                <i class="fas fa-info-circle"></i>
                No se encontraron productos.
            </div>
        `;
        return;
    }

    let html = '<div class="table-container"><table class="table">';
    html += `
        <thead>
            <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
                <th>Fabricante</th>
                <th>País</th>
                <th>Fecha Import.</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
    `;

    products.forEach(producto => {
        const fecha = producto.fechaImportacion ? new Date(producto.fechaImportacion).toLocaleDateString('es-ES') : 'N/A';
        html += `
            <tr class="fade-in">
                <td>${producto.id}</td>
                <td><strong>${producto.nombre}</strong></td>
                <td><span class="badge badge-secondary">${producto.categoria}</span></td>
                <td><strong>$${producto.precio.toFixed(2)}</strong></td>
                <td>${producto.fabricante}</td>
                <td>${producto.paisOrigen}</td>
                <td>${fecha}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn-icon btn-icon-edit" onclick="editProduct(${producto.id})" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn-icon btn-icon-delete" onclick="deleteProduct(${producto.id})" title="Eliminar">
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

// Filtrar productos
function filterProducts() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const category = document.getElementById('categoryFilter').value;
    const country = document.getElementById('countryFilter').value;

    let filtered = allProducts.filter(producto => {
        const matchesSearch = producto.nombre.toLowerCase().includes(searchTerm) ||
                            producto.fabricante.toLowerCase().includes(searchTerm);
        const matchesCategory = !category || producto.categoria === category;
        const matchesCountry = !country || producto.paisOrigen === country;

        return matchesSearch && matchesCategory && matchesCountry;
    });

    displayProducts(filtered);
}

// Limpiar filtros
function clearFilters() {
    document.getElementById('searchInput').value = '';
    document.getElementById('categoryFilter').value = '';
    document.getElementById('countryFilter').value = '';
    displayProducts(allProducts);
}

// Abrir modal para agregar producto
function openAddProductModal() {
    currentEditId = null;
    document.getElementById('modalTitle').textContent = 'Nuevo Producto';
    document.getElementById('productForm').reset();
    document.getElementById('productId').value = '';

    // Establecer fecha actual por defecto
    document.getElementById('productImportDate').value = new Date().toISOString().split('T')[0];

    document.getElementById('productModal').classList.add('active');
}

// Editar producto
async function editProduct(id) {
    try {
        const response = await fetch(`${API_BASE}/productos/${id}`);
        const producto = await response.json();

        currentEditId = id;
        document.getElementById('modalTitle').textContent = 'Editar Producto';
        document.getElementById('productId').value = producto.id;
        document.getElementById('productName').value = producto.nombre;
        document.getElementById('productCategory').value = producto.categoria;
        document.getElementById('productPrice').value = producto.precio;
        document.getElementById('productManufacturer').value = producto.fabricante;
        document.getElementById('productCountry').value = producto.paisOrigen;
        document.getElementById('productDescription').value = producto.descripcion || '';

        if (producto.fechaImportacion) {
            document.getElementById('productImportDate').value = producto.fechaImportacion;
        }

        document.getElementById('productModal').classList.add('active');

    } catch (error) {
        console.error('Error cargando producto:', error);
        alert('Error al cargar el producto');
    }
}

// Guardar producto (crear o actualizar)
async function saveProduct() {
    const form = document.getElementById('productForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const producto = {
        nombre: document.getElementById('productName').value,
        categoria: document.getElementById('productCategory').value,
        precio: parseFloat(document.getElementById('productPrice').value),
        fabricante: document.getElementById('productManufacturer').value,
        paisOrigen: document.getElementById('productCountry').value,
        descripcion: document.getElementById('productDescription').value,
        fechaImportacion: document.getElementById('productImportDate').value
    };

    try {
        let response;
        if (currentEditId) {
            // Actualizar producto existente
            response = await fetch(`${API_BASE}/productos/${currentEditId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(producto)
            });
        } else {
            // Crear nuevo producto
            response = await fetch(`${API_BASE}/productos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(producto)
            });
        }

        if (response.ok) {
            closeProductModal();
            loadProducts();
            showSuccessMessage(currentEditId ? 'Producto actualizado exitosamente' : 'Producto creado exitosamente');
        } else {
            throw new Error('Error al guardar el producto');
        }

    } catch (error) {
        console.error('Error guardando producto:', error);
        alert('Error al guardar el producto');
    }
}

// Eliminar producto
async function deleteProduct(id) {
    if (!confirm('¿Está seguro de que desea eliminar este producto?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/productos/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadProducts();
            showSuccessMessage('Producto eliminado exitosamente');
        } else {
            throw new Error('Error al eliminar el producto');
        }

    } catch (error) {
        console.error('Error eliminando producto:', error);
        alert('Error al eliminar el producto');
    }
}

// Cerrar modal
function closeProductModal() {
    document.getElementById('productModal').classList.remove('active');
    currentEditId = null;
}

// Cerrar modal al hacer clic fuera
document.getElementById('productModal').addEventListener('click', function(e) {
    if (e.target === this) {
        closeProductModal();
    }
});

// Mensajes de éxito/error
function showSuccessMessage(message) {
    // Implementación simple, podrías usar una librería de notificaciones
    alert(message);
}

function showErrorMessage(message) {
    alert(message);
}
