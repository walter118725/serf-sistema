// Reportes JavaScript - SERF Sistema
const API_BASE = '/api';

// Cargar datos al iniciar
document.addEventListener('DOMContentLoaded', function() {
    // Event listener para cambio de tipo de reporte
    document.getElementById('reportType').addEventListener('change', updateReportOptions);
});

// Actualizar opciones según el tipo de reporte
function updateReportOptions() {
    const reportType = document.getElementById('reportType').value;
    const countrySection = document.getElementById('countrySection');
    const securityOptions = document.getElementById('securityOptions');

    // Mostrar/ocultar sección de país
    if (reportType === 'ventas' || reportType === 'inventario' || reportType === 'seguro') {
        countrySection.style.display = 'block';
    } else {
        countrySection.style.display = 'none';
    }

    // Mostrar/ocultar opciones de seguridad
    if (reportType === 'seguro') {
        securityOptions.style.display = 'block';
    } else {
        securityOptions.style.display = 'none';
    }
}

// Generar reporte
async function generateReport() {
    const reportType = document.getElementById('reportType').value;

    if (!reportType) {
        alert('Por favor seleccione un tipo de reporte');
        return;
    }

    try {
        let url = `${API_BASE}/reportes/`;
        let params = new URLSearchParams();

        switch (reportType) {
            case 'ventas':
                const ventasPais = document.getElementById('reportCountry').value;
                if (!ventasPais) {
                    alert('Por favor seleccione un país para el reporte de ventas');
                    return;
                }
                url += `ventas/${ventasPais}`;
                break;

            case 'inventario':
                const invPais = document.getElementById('reportCountry').value;
                if (!invPais) {
                    alert('Por favor seleccione un país para el reporte de inventario');
                    return;
                }
                url += `inventario/${invPais}`;
                break;

            case 'consolidado':
                url += 'consolidado';
                break;

            case 'seguro':
                const seguroPais = document.getElementById('reportCountry').value;
                if (!seguroPais) {
                    alert('Por favor seleccione un país para el reporte seguro');
                    return;
                }
                url += 'seguro';
                params.append('pais', seguroPais);

                if (document.getElementById('addSignature').checked) {
                    params.append('firmar', 'true');
                }
                if (document.getElementById('addWatermark').checked) {
                    params.append('marcaAgua', 'true');
                }
                if (document.getElementById('encryptReport').checked) {
                    params.append('encriptar', 'true');
                }
                break;
        }

        // Agregar parámetros a la URL si existen
        const queryString = params.toString();
        if (queryString) {
            url += '?' + queryString;
        }

        // Hacer la petición
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Error al generar el reporte');
        }

        const reportData = await response.text();

        // Mostrar el reporte
        displayReportResult(reportData);

    } catch (error) {
        console.error('Error generando reporte:', error);
        alert('Error al generar el reporte. Por favor intente nuevamente.');
    }
}

// Generar reporte rápido (sin configuración)
async function quickReport(type) {
    try {
        let url = `${API_BASE}/reportes/`;

        switch (type) {
            case 'ventas':
                // Generar reporte de ventas para Perú por defecto
                url += 'ventas/PERU';
                break;

            case 'inventario':
                // Generar reporte de inventario para Perú por defecto
                url += 'inventario/PERU';
                break;

            case 'consolidado':
                url += 'consolidado';
                break;

            case 'seguro':
                // Generar reporte seguro para Perú con todas las opciones
                url += 'seguro?pais=PERU&firmar=true&marcaAgua=true&encriptar=true';
                break;
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Error al generar el reporte');
        }

        const reportData = await response.text();
        displayReportResult(reportData);

    } catch (error) {
        console.error('Error generando reporte:', error);
        alert('Error al generar el reporte. Por favor intente nuevamente.');
    }
}

// Mostrar resultado del reporte
function displayReportResult(reportData) {
    const resultCard = document.getElementById('reportResultCard');
    const resultContent = document.getElementById('reportResult');

    resultContent.textContent = reportData;
    resultCard.style.display = 'block';

    // Scroll al resultado
    resultCard.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

// Cerrar resultado del reporte
function closeReportResult() {
    document.getElementById('reportResultCard').style.display = 'none';
}

// Cargar reportes disponibles
function loadAvailableReports() {
    // Esta función podría cargar historial de reportes generados
    // Por ahora solo muestra un mensaje de confirmación
    alert('Reportes actualizados');
}

// Función para descargar reporte (futura implementación)
function downloadReport(format) {
    alert(`Funcionalidad de descarga en formato ${format} - Próximamente disponible`);
}
