# SERF Sistema

Aplicación Spring Boot 3.5.7 (Java 21) para gestión de inventario, productos y ventas en Financorp.

## Tecnologías
- Spring Boot 3.5.7 (Web, Data JPA, Validation)
- Hibernate (gestionado por BOM de Spring Boot)
- Base de datos H2 en memoria
- Maven

## Ejecutar
- Puerto: 8080
- H2 Console: /h2-console (JDBC URL: `jdbc:h2:mem:serfdb`, usuario `sa`, sin contraseña)

### Build
- Compilar: `mvn clean compile`
- Ejecutar: `mvn spring-boot:run`

## Endpoints
- API de productos, inventario y ventas bajo `/api/...`

## Configuración
Propiedades personalizadas en `application.properties`:
```
app.nombre=SERF
app.version=1.0.0
```

## Notas
- Dependencias de Hibernate se administran por el BOM de Spring Boot (no versionar manualmente).
- Se agregó `spring-boot-configuration-processor` para metadata de propiedades.
