<div align="center">

# APR Xàtiva — Backend

**API REST del sistema de control d'accés a les Àrees de Prioritat Residencial**  
*REST API for the Residential Priority Areas access control system*

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)](https://mysql.com)
[![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)](https://docker.com)
[![Tests](https://img.shields.io/badge/Tests-32_unitaris-brightgreen)](https://junit.org)

</div>

---

## ¿Qué es APR Xàtiva?

Sistema de gestión de Áreas de Prioridad Residencial para el Ayuntamiento de Xàtiva. Permite controlar el acceso de vehículos al nucli antic mediante un sistema de solicitudes, acreditaciones y derechos de acceso gestionados desde un panel web y una app móvil Android.

> Proyecto TFG del CFGS DAM en el IES Dr. Lluís Simarro — nota **9/10**. Cedido al Ayuntamiento de Xàtiva como base para una posible implantación real.

---

## ✨ Funcionalidades principales

### 🔐 Autenticación y seguridad
- JWT stateless con **HMAC-SHA384** (tokens de 15 minutos)
- **Refresh tokens** persistentes en base de datos (30 días) con revocación en logout
- Rate limiting con **Bucket4j** — 10 intentos/minuto en `/api/auth/**`
- Cifrado BCrypt para contraseñas
- Headers de seguridad y CORS configurado

### 👥 Gestión de usuarios
- Registro público con solicitud automática en estado PENDIENTE
- Registro desde panel admin con activación inmediata
- Roles USER / ADMIN
- Activación / desactivación de cuentas

### 📋 Solicitudes de autorización
- Flujo completo: PENDIENTE → APROBADA / RECHAZADA
- Al aprobar, el usuario queda activado automáticamente
- Historial con fecha de gestión y admin responsable

### 🚗 Vehículos
- Alta / baja / reactivación por usuario
- Tipos de acreditación: LIBRE / ACREDITADO
- Validación de formato de matrícula española (nueva y antigua)
- Validación de formato DNI / NIE

### 🎫 Derechos de acceso
- **Permanentes** — válidos 1 año, vinculados a un vehículo propio
- **Puntuales** — para un día concreto, vehículo propio o matrícula invitado
- Límite de 5 invitaciones por mes
- Restricción de fecha: solo dentro del mes siguiente
- Desactivación automática nocturna de derechos expirados

### 📄 Documentación
- Subida de ficheros (empadronamiento, etc.) hasta 10MB
- Descarga protegida por rol ADMIN

### 📷 Simulador de cámaras APR
- Endpoint que simula el paso de vehículos por las cámaras
- 60% de probabilidad de matrícula real con derecho activo
- 40% de matrícula aleatoria sin acceso

### 📊 Auditoría
- Log asíncrono (`@Async`) de todas las acciones relevantes
- Eventos: LOGIN, LOGOUT, REGISTRO_USUARIO, SOLICITUD_APROBADA, SOLICITUD_RECHAZADA, VEHICULO_ALTA, VEHICULO_BAJA, DERECHO_PERMANENTE_CREADO, DERECHO_PUNTUAL_CREADO, DERECHO_INVITADO_CREADO, DERECHO_ELIMINADO
- Endpoint paginado `/api/auditoria` con filtro por evento

### 🗑️ Tareas programadas
- Eliminación de usuarios sin documentación tras 14 días (diaria)
- Desactivación de derechos expirados (diaria)

---

## 🛠️ Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Seguridad | Spring Security 7 + JWT (jjwt 0.12.6) |
| Base de datos | MySQL 8 |
| ORM | Spring Data JPA + Hibernate 7 |
| Rate limiting | Bucket4j 8.10.1 |
| Documentación | Swagger / OpenAPI 3.1 (SpringDoc) |
| Tests | JUnit 5 + Mockito (32 tests) |
| Contenedores | Docker + Docker Compose |

---

## 📁 Estructura del proyecto

```
src/main/java/com/alroca/apr_xativa/
├── config/
│   ├── CorsConfig.java
│   ├── GlobalExceptionHandler.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DerechoAccesoController.java
│   ├── DocumentoController.java
│   ├── SolicitudController.java
│   ├── UsuarioController.java
│   └── VehiculoController.java
├── entity/
│   ├── AuditoriaLog.java
│   ├── DerechoAcceso.java
│   ├── Documento.java
│   ├── RefreshToken.java
│   ├── Solicitud.java
│   ├── Usuario.java
│   └── Vehiculo.java
├── exception/
├── mapper/
├── repository/
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   ├── RateLimitingFilter.java
│   └── SecurityConfig.java
├── service/
├── simulador/
└── utils/
    ├── SecurityUtils.java
    └── ValidacionUtils.java
```

---

## 📡 Endpoints principals

| Método | Ruta | Descripción | Rol |
|--------|------|-------------|-----|
| POST | `/api/auth/login` | Login + JWT + refresh token | Público |
| POST | `/api/auth/refresh` | Renovar JWT con refresh token | Público |
| POST | `/api/auth/logout` | Revocar refresh token | Autenticado |
| POST | `/api/usuarios/registro` | Registro público | Público |
| GET | `/api/usuarios` | Listar usuarios (paginado) | ADMIN |
| GET | `/api/solicitudes/pendientes` | Solicitudes pendientes | ADMIN |
| PUT | `/api/solicitudes/{id}/aprobar` | Aprobar solicitud | ADMIN |
| GET | `/api/vehiculos` | Vehículos del usuario | Autenticado |
| GET | `/api/derechos` | Derechos de acceso | Autenticado |
| GET | `/api/auditoria` | Log de auditoría (paginado) | ADMIN |
| GET | `/api/simulador/comprobar` | Simular paso por cámara | ADMIN |

Documentación completa disponible en `/swagger-ui.html` con el servidor en marcha.

---

## 🚀 Instalación con Docker (recomendado)

```bash
git clone https://github.com/ArocaDev/apr-xativa-backend.git
cd apr-xativa-backend
docker-compose up --build -d
```

Accede en `http://localhost:8080`

---

## 🚀 Instalación local

```bash
git clone https://github.com/ArocaDev/apr-xativa-backend.git
cd apr-xativa-backend
```

Crea la base de datos ejecutando `docker/apr_xativa.sql` en MySQL, luego:

```bash
# Edita src/main/resources/application.properties con tus credenciales
./mvnw spring-boot:run
```

---

## 🔑 Configuración principal

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/apr_xativa
spring.datasource.username=root
spring.datasource.password=

jwt.secret=           # Base64, mínimo 256 bits
jwt.expiration=900000 # 15 minutos en ms

app.upload.dir=uploads/documentos
```

---

## 🧪 Tests

```bash
./mvnw test
```

32 tests unitarios con JUnit 5 + Mockito.

---

## 🗺️ Roadmap

- [ ] Despliegue en Railway con dominio apr.aroca.dev
- [ ] WebSockets para notificaciones en tiempo real
- [ ] Métricas con Micrometer + Prometheus
- [ ] Observabilidad con OpenTelemetry

---

## 🔗 Repositorios del proyecto

| Componente | Repositorio |
|---|---|
| Backend API REST (este repo) | [apr-xativa-backend](https://github.com/ArocaDev/apr-xativa-backend) |
| Panel web + Landing | [apr-xativa-web](https://github.com/ArocaDev/apr-xativa-web) |
| App móvil Android | [apr-xativa-android](https://github.com/ArocaDev/apr-xativa-android) |

---

## 👤 Autor

**Alejandro Rodríguez Calabuig**  
[github.com/ArocaDev](https://github.com/ArocaDev) · [LinkedIn](https://linkedin.com/in/alejandro-rodriguez-calabuig-a871a1230)

---

## 📄 Licencia

Proyecto académico — cedido al Ayuntamiento de Xàtiva para posible uso institucional.
