<div align="center">

# APR Xàtiva — Backend

**API REST para el sistema de control de acceso vehicular del Ajuntament de Xàtiva**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.5-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org)
[![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)](https://mysql.com)
[![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)](https://docker.com)

</div>

---

## ¿Qué es APR Xàtiva?

APR Xàtiva es un sistema de control de acceso vehicular desarrollado como Trabajo de Fin de Grado (DAM) y adoptado por el Ajuntament de Xàtiva como propuesta técnica. Permite gestionar qué vehículos pueden acceder a zonas restringidas de la ciudad, con un panel web para administradores y una app Android para los agentes de control.

🌐 **Panel web:** [github.com/ArocaDev/apr-xativa-frontend](https://github.com/ArocaDev/apr-xativa-frontend)  
📱 **App Android:** [github.com/ArocaDev/apr-xativa-android](https://github.com/ArocaDev/apr-xativa-android)

---

## ✨ Funcionalidades

- **Autenticación JWT** con access token (15 min) y refresh token (7 días)
- **Control de acceso vehicular** — consulta por matrícula con respuesta inmediata
- **Gestión de vehículos** — CRUD completo con roles y permisos
- **Gestión de usuarios** — roles ADMIN y AGENTE
- **Solicitudes de acceso** — flujo de alta, aprobación y rechazo
- **Acceso puntual para invitados** — hasta 5 invitaciones mensuales por usuario
- **Simulador de acceso** — probabilidad 60/40 para testing
- **Auditoría asíncrona** — registro de todas las operaciones con `@Async` sin impacto en rendimiento
- **Rate limiting** con Bucket4j en `/api/auth/**` (10 intentos/minuto por IP)
- **Paginación opcional** retrocompatible en todos los listados
- **32 tests unitarios**
- **Documentación Swagger/OpenAPI 3.1**

---

## 🗂️ Diagramas

### Arquitectura del sistema
![Arquitectura](assets/diagrama_arquitectura.svg)

### Flujo de autenticación JWT
![JWT](assets/flux_autenticacion_jwt.svg)

### Flujo de solicitud de acceso
![Acceso](assets/flux_solicitud_acceso.svg)

---

## 🛠️ Stack técnico

| Capa | Tecnología |
|------|-----------|
| Framework | Spring Boot 4.0.5 |
| Lenguaje | Java 21 |
| Base de datos | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Auth | JWT (jjwt) + Refresh Tokens |
| Rate limiting | Bucket4j 8.10.1 |
| Auditoría | Spring `@Async` + `AuditoriaLog` |
| Paginación | Spring Data `Page<T>` + `Pageable` |
| Contenedores | Docker + Docker Compose |
| Documentación | Swagger / OpenAPI 3.1 |
| Tests | JUnit 5 + Mockito (32 tests) |

---

## 📁 Estructura del proyecto

```
apr-xativa-backend/
├── src/main/java/com/arocadev/apr/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── VehiculoController.java
│   │   ├── UsuarioController.java
│   │   ├── SolicitudController.java
│   │   ├── DerechoAccesoController.java
│   │   ├── AccesoController.java
│   │   └── AuditoriaController.java
│   ├── service/
│   │   ├── RefreshTokenService.java
│   │   ├── AuditoriaService.java
│   │   └── ...
│   ├── repository/
│   │   ├── RefreshTokenRepository.java
│   │   ├── AuditoriaLogRepository.java
│   │   └── ...
│   ├── model/
│   │   ├── RefreshToken.java
│   │   ├── AuditoriaLog.java
│   │   └── ...
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── JwtFilter.java
│   │   └── RateLimitingFilter.java
│   └── config/
│       └── AsyncConfig.java
├── assets/
│   ├── diagrama_arquitectura.svg
│   ├── flux_autenticacion_jwt.svg
│   └── flux_solicitud_acceso.svg
├── docker-compose.yml
├── Dockerfile
├── .env.example
└── pom.xml
```

---

## 🚀 Instalación con Docker

```bash
git clone https://github.com/ArocaDev/apr-xativa-backend.git
cd apr-xativa-backend
cp .env.example .env
# Edita .env con tus credenciales
docker compose up --build -d
```

API disponible en `http://localhost:8080`  
Swagger en `http://localhost:8080/swagger-ui.html`

---

## 🚀 Instalación local

```bash
git clone https://github.com/ArocaDev/apr-xativa-backend.git
cd apr-xativa-backend
# Crea la base de datos MySQL
# Edita src/main/resources/application.properties
./mvnw spring-boot:run
```

---

## 🔑 Variables de entorno

```env
DB_URL=jdbc:mysql://localhost:3306/apr_xativa
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000
```

---

## 📡 Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Login → JWT + refresh token |
| POST | `/api/auth/refresh` | Renovar access token |
| POST | `/api/auth/logout` | Invalidar refresh token |
| GET | `/api/vehiculos` | Listar vehículos (paginable) |
| POST | `/api/vehiculos` | Registrar vehículo |
| GET | `/api/vehiculos/{matricula}/acceso` | Consultar acceso |
| POST | `/api/acceso/invitado` | Acceso puntual invitado |
| GET | `/api/usuarios` | Listar usuarios (paginable) |
| GET | `/api/solicitudes` | Listar solicitudes (paginable) |
| PUT | `/api/solicitudes/{id}/aprobar` | Aprobar solicitud |
| PUT | `/api/solicitudes/{id}/rechazar` | Rechazar solicitud |
| GET | `/api/auditoria` | Log de auditoría — filtros por DNI y tipo |

---

## 🧪 Tests

```bash
./mvnw test
```

32 tests unitarios con JUnit 5 + Mockito.

---

## 🗺️ Roadmap

- [x] Auth JWT con access token (15 min) y refresh token (7 días)
- [x] Endpoints `/api/auth/refresh` y `/api/auth/logout`
- [x] Rate limiting con Bucket4j (10 intentos/min por IP en `/api/auth/**`)
- [x] Control de acceso vehicular por matrícula
- [x] Roles ADMIN y AGENTE
- [x] Solicitudes de acceso con flujo de aprobación/rechazo
- [x] Acceso puntual para invitados (5/mes)
- [x] Simulador con probabilidad 60/40
- [x] Auditoría asíncrona con `@Async` — alta, baja, aprobación, rechazo
- [x] Paginación opcional retrocompatible
- [x] 32 tests unitarios
- [x] Swagger / OpenAPI 3.1
- [x] Docker Compose

---

## 🏆 Reconocimiento

Proyecto calificado con **10/10** y adoptado por el **Ajuntament de Xàtiva** como propuesta técnica oficial.

---

## 👤 Autor

**Alejandro Rodríguez Calabuig**  
[github.com/ArocaDev](https://github.com/ArocaDev) · [LinkedIn](https://www.linkedin.com/in/alejandro-rodriguez-calabuig-a871a1230)

---

## 📄 Licencia

Proyecto académico — no licenciado para uso comercial.
