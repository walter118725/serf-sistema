# 🏢 Sistema Empresarial de Gestión de Reportes Financieros (SERF)

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?style=for-the-badge&logo=springboot)
![H2 Database](https://img.shields.io/badge/H2-Database-blue?style=for-the-badge&logo=h2)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**Sistema empresarial completo para gestión de reportes financieros multi-filial**

[Características](#-características-principales) • [Instalación](#-instalación) • [Uso](#-uso) • [API](#-api-rest) • [Arquitectura](#-arquitectura)

</div>

## 📋 Descripción

SERF es un sistema empresarial robusto diseñado para **FinanCorp S.A.**, que permite la gestión centralizada de productos, ventas y reportes financieros across múltiples filiales internacionales. El sistema implementa **6 patrones de diseño GoF** y proporciona conversión automática de monedas a estándar corporativo EUR.

### 🎯 Objetivos del Sistema

- **Consolidación corporativa**: Unificación de reportes de todas las filiales
- **Gestión multi-moneda**: Conversión automática CNY/PEN/USD/MXN/COP → EUR
- **Control de inventario**: Gestión de stock en tiempo real
- **Reportes estandarizados**: Generación automática con seguridad documental
- **Escalabilidad empresarial**: Arquitectura preparada para crecimiento

## ✨ Características Principales

### 🏪 Gestión Multi-Filial
- **5 países soportados**: China, Perú, Estados Unidos, México, Colombia
- **Conversión automática** de monedas locales a EUR corporativo
- **Reportes consolidados** para dirección ejecutiva
- **Configuración centralizada** con patrón Singleton

### 📦 Gestión de Productos
- ✅ **CRUD completo**: Crear, leer, actualizar, eliminar productos
- ✅ **Control de stock**: Seguimiento automático de inventario
- ✅ **Categorización**: Laptops, Smartphones, Equipos de Red, Accesorios
- ✅ **Búsqueda avanzada**: Por código, nombre, categoría, proveedor
- ✅ **Alertas de stock bajo**: Notificaciones automáticas

### 💰 Registro de Ventas
- ✅ **Facturación automática**: Generación de números únicos
- ✅ **Multi-moneda**: Soporte para 5 monedas locales
- ✅ **Validaciones**: Control de stock disponible
- ✅ **Trazabilidad completa**: Vendedor, cliente, método de pago
- ✅ **Actualización automática**: Stock actualizado en tiempo real

### 📊 Sistema de Reportes
- ✅ **Exportación CSV**: Datos completos descargables
- ✅ **Reportes para impresión**: Formato corporativo profesional
- ✅ **Dashboard en tiempo real**: Métricas y KPIs actualizados
- ✅ **Seguridad documental**: Marcas de agua y firmas digitales SHA-256
- ✅ **Reportes por período**: Mensual, trimestral, anual

### 🎨 Interfaz de Usuario
- ✅ **Diseño responsive**: Compatible con desktop, tablet, móvil
- ✅ **Navegación intuitiva**: Menú lateral con iconografía profesional
- ✅ **Modales funcionales**: Para todas las operaciones CRUD
- ✅ **Feedback visual**: Toasts, loading indicators, confirmaciones
- ✅ **Tema corporativo**: Branding consistente de FinanCorp

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 21 LTS** - Lenguaje de programación principal
- **Spring Boot 3.5.7** - Framework empresarial
- **Spring Data JPA** - Persistencia de datos
- **Hibernate 6.6** - ORM avanzado
- **H2 Database** - Base de datos embebida para desarrollo
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de boilerplate

### Frontend
- **HTML5 + CSS3** - Estructura y estilos modernos
- **JavaScript ES6+** - Lógica de cliente avanzada
- **Thymeleaf** - Template engine server-side
- **Font Awesome** - Iconografía profesional
- **CSS Grid + Flexbox** - Layout responsivo

### Patrones de Diseño Implementados
1. **Singleton** - `ConfiguracionGlobal` para configuración centralizada
2. **Prototype** - `ReportePrototype` para clonación de reportes
3. **Builder** - `ReporteBuilder` para construcción compleja
4. **Composite** - `SeccionReporte` para estructura jerárquica
5. **Decorator** - `MarcaAguaDecorator` + `FirmaDigitalDecorator` para seguridad
6. **Facade** - `ReporteFacade` para simplificación de operaciones

## 🚀 Instalación

### Prerrequisitos
- **Java 21 LTS** o superior
- **Maven 3.8+** o usar el wrapper incluido
- **Git** para clonación del repositorio

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/walter118725/serf-sistema.git
cd serf-sistema
```

2. **Compilar el proyecto**
```bash
./mvnw clean compile
```

3. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

4. **Acceder al sistema**
- **Aplicación**: http://localhost:9090
- **Consola H2**: http://localhost:9090/h2-console
  - URL JDBC: `jdbc:h2:mem:serfdb`
  - Usuario: `sa`
  - Password: *(vacío)*

## 💻 Uso

### 🏠 Dashboard Principal
Accede a http://localhost:9090 para ver el dashboard principal con:
- Estadísticas en tiempo real
- Gráficos de ventas
- Indicadores de stock
- Navegación a todas las secciones

### 📦 Gestión de Productos
1. **Crear producto**: Clic en "Nuevo Producto" → Completar formulario → Guardar
2. **Editar producto**: Clic en ✏️ → Modificar datos → Actualizar
3. **Eliminar producto**: Clic en 🗑️ → Confirmar eliminación

### 💰 Registro de Ventas
1. **Nueva venta**: Clic en "Nueva Venta"
2. **Seleccionar producto**: Dropdown con productos disponibles
3. **Completar datos**: Cliente, cantidad, vendedor, país
4. **Guardar**: Sistema actualiza stock automáticamente

### 📊 Reportes y Exportación
1. **Exportar CSV**: Botón "Exportar" en cualquier sección
2. **Imprimir**: Botón "Imprimir" para reportes formateados
3. **Dashboard**: Métricas actualizadas automáticamente

## 🔌 API REST

### Productos
```http
GET    /api/productos              # Listar todos los productos
GET    /api/productos/{id}         # Obtener producto por ID
POST   /api/productos              # Crear nuevo producto
PUT    /api/productos/{id}         # Actualizar producto
DELETE /api/productos/{id}         # Eliminar producto
GET    /api/productos/bajo-stock   # Productos con stock bajo
```

### Ventas
```http
GET    /api/ventas                 # Listar todas las ventas
POST   /api/ventas                 # Registrar nueva venta
GET    /api/ventas/estadisticas    # Obtener estadísticas
GET    /api/ventas/pais/{pais}     # Ventas por país
GET    /api/ventas/fecha           # Ventas por rango de fechas
```

### Ejemplos de Uso

**Crear Producto:**
```json
POST /api/productos
{
  "codigo": "LAPTOP-001",
  "nombre": "MacBook Pro 14\"",
  "categoria": "LAPTOP",
  "proveedor": "Apple Inc.",
  "stockInicial": 50,
  "costoImportacion": 1500.00,
  "precioVentaSugerido": 2000.00,
  "monedaOrigen": "USD",
  "descripcionTecnica": "M3 Pro, 18GB RAM, 512GB SSD"
}
```

**Registrar Venta:**
```json
POST /api/ventas
{
  "numeroFactura": "FAC-20251104010630",
  "fechaVenta": "2025-11-04T06:30:00",
  "productoId": 1,
  "cantidad": 2,
  "precioUnitario": 2000.00,
  "monedaLocal": "USD",
  "cliente": "TechCorp Solutions",
  "vendedorResponsable": "Ana García",
  "paisFilial": "Estados Unidos",
  "metodoPago": "Transferencia"
}
```

## 🏗️ Arquitectura

### Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/financorp/serf/
│   │   ├── builder/           # Patrón Builder
│   │   ├── composite/         # Patrón Composite
│   │   ├── config/            # Patrón Singleton
│   │   ├── controller/        # Controladores REST
│   │   ├── facade/            # Patrón Facade
│   │   ├── model/             # Entidades JPA
│   │   ├── repository/        # Repositorios Spring Data
│   │   └── service/           # Lógica de negocio
│   └── resources/
│       ├── static/            # CSS, JS, imágenes
│       ├── templates/         # Plantillas Thymeleaf
│       └── application.properties
└── test/                      # Pruebas unitarias
```

### Flujo de Datos
```
Frontend (Thymeleaf + JS) 
    ↓
Controllers (REST API)
    ↓
Services (Lógica de Negocio)
    ↓
Repositories (Spring Data JPA)
    ↓
H2 Database (En memoria)
```

### Patrones Implementados

#### 🔧 Singleton - ConfiguracionGlobal
```java
@Component
public class ConfiguracionGlobal {
    private static ConfiguracionGlobal instance;
    
    public static ConfiguracionGlobal getInstance() {
        if (instance == null) {
            instance = new ConfiguracionGlobal();
        }
        return instance;
    }
    
    public BigDecimal convertirAMonedaCorporativa(BigDecimal monto, String monedaOrigen) {
        // Lógica de conversión...
    }
}
```

#### 🏗️ Builder - ReporteBuilder
```java
public class ReporteBuilder {
    public ReporteBuilder conTitulo(String titulo) { /* ... */ }
    public ReporteBuilder conSeccion(SeccionReporte seccion) { /* ... */ }
    public ReporteBuilder conFirmaDigital() { /* ... */ }
    public Reporte construir() { /* ... */ }
}
```

## 🧪 Desarrollo y Pruebas

### Ejecutar Pruebas
```bash
./mvnw test
```

### Modo Desarrollo
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Construcción para Producción
```bash
./mvnw clean package -DskipTests
java -jar target/serf-sistema-0.0.1-SNAPSHOT.jar
```

## 📊 Características Empresariales

### 💱 Sistema Multi-Moneda
| Moneda | País | Conversión a EUR |
|--------|------|------------------|
| CNY | China | Automática |
| PEN | Perú | Automática |
| USD | Estados Unidos | Automática |
| MXN | México | Automática |
| COP | Colombia | Automática |

### 🔒 Seguridad Documental
- **Marcas de agua**: Identificación corporativa en reportes
- **Firmas digitales**: SHA-256 para integridad de documentos
- **Trazabilidad**: Registro completo de todas las operaciones
- **Validaciones**: Frontend y backend para integridad de datos

### 📈 Métricas y KPIs
- Total de productos activos
- Ventas realizadas por período
- Ingresos consolidados en EUR
- Productos con stock bajo
- Ventas por filial/país
- Rendimiento por vendedor

## 🤝 Contribución

### Guía de Contribución
1. Fork del repositorio
2. Crear rama para feature: `git checkout -b feature/nueva-funcionalidad`
3. Commit de cambios: `git commit -am 'Agregar nueva funcionalidad'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

### Estándares de Código
- **Java**: Seguir convenciones de Oracle
- **Spring**: Usar anotaciones apropiadas
- **Tests**: Cobertura mínima 80%
- **Documentación**: Javadoc para métodos públicos

## 📝 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 👥 Equipo de Desarrollo

**Desarrollado para FinanCorp S.A.**

- **Arquitectura**: Java 21 + Spring Boot 3.5.7
- **Patrones**: GoF Design Patterns implementados
- **Frontend**: Responsive Web Design
- **Base de Datos**: H2 + JPA/Hibernate
- **Seguridad**: SHA-256 + Validaciones

## 📞 Soporte

Para soporte técnico o consultas:

- **Issues**: [GitHub Issues](https://github.com/walter118725/serf-sistema/issues)
- **Wiki**: [Documentación Técnica](https://github.com/walter118725/serf-sistema/wiki)
- **Releases**: [Versiones](https://github.com/walter118725/serf-sistema/releases)

---

<div align="center">

**🏢 Sistema Empresarial de Gestión de Reportes Financieros (SERF)**

*Solución integral para gestión corporativa multi-filial*

[![Developed for FinanCorp](https://img.shields.io/badge/Developed%20for-FinanCorp%20S.A.-blue?style=for-the-badge)](http://localhost:9090)

</div>
