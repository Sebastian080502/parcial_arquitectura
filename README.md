# 🏛️ ERP Iglesias — Refactorización Arquitectónica

> **Parcial 1 — Arquitectura de Software · Sexto Semestre**  
> Auditoría, documentación y refactorización incremental del sistema ERP Iglesias

---

## 👥 Integrantes

| Nombre | Rol |
|--------|-----|
| Juan Sebastián Osorio Fierro | Desarrollador / Arquitecto |
| Karina Cantillo Plaza | Desarrolladora / Arquitecta |

---

## 📋 Descripción del Proyecto

**ERP Iglesias** es un sistema de gestión administrativa para iglesias que permite administrar miembros, cursos, inscripciones, ofrendas y pagos, con control de acceso basado en roles (`ADMIN` y `CLIENT`).

Este repositorio documenta el proceso de **auditoría arquitectónica** del sistema, identificando problemas de diseño y aplicando mejoras mediante principios **SOLID**, patrones de diseño y **Architecture Decision Records (ADR)**.

La refactorización se realizó de forma **incremental**, garantizando que el comportamiento funcional del sistema no se vea afectado en ningún momento.

---

## 🎯 Objetivos

- ✅ Identificar problemas arquitectónicos del sistema original
- ✅ Proponer 10 mejoras documentadas mediante ADRs
- ✅ Implementar 5 de los 10 cambios propuestos
- ✅ Evidenciar cada cambio con commits descriptivos y pruebas funcionales
- ✅ Mejorar la mantenibilidad, legibilidad y escalabilidad del código

---

## 🛠️ Stack Tecnológico

### Backend
| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.3 | Framework backend |
| Spring Security + JWT | jjwt 0.11.5 | Autenticación stateless |
| Spring Data JPA + Hibernate | Hibernate 6 | ORM y persistencia |
| PostgreSQL | Latest | Base de datos relacional |
| Maven | 3.x | Build y dependencias |

### Frontend
| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Angular | 17.3 | Framework SPA |
| Angular Material | 17 | Componentes UI |
| TypeScript | 5.4 | Tipado estático |
| RxJS | 7.8 | Programación reactiva |

### Infraestructura
| Tecnología | Uso |
|-----------|-----|
| Docker + Docker Compose | Contenedores y orquestación |
| Nginx | Reverse proxy para Angular |

---

## 🏗️ Arquitectura del Sistema

El sistema sigue una arquitectura de **capas** con dependencias unidireccionales:

```
┌─────────────────────────────────┐
│         Angular (SPA)           │  ← Frontend
│  services/ · models/ · components/  │
└──────────────┬──────────────────┘
               │ HTTP / JWT
┌──────────────▼──────────────────┐
│        Spring Boot API          │  ← Backend
│  controller → service → repository  │
│       entity · dto · exception  │
└──────────────┬──────────────────┘
               │ JPA
┌──────────────▼──────────────────┐
│          PostgreSQL             │  ← Base de datos
└─────────────────────────────────┘
```

---

## 🗄️ Modelo Entidad-Relación

```
┌──────────┐        ┌──────────┐        ┌──────────┐
│  Church  │──1:N──▶│  Person  │──1:N──▶│ Offering │
└──────────┘        └────┬─────┘        └──────────┘
     │                   │
     │ 1:N               │ N:M (via Enrollment)
     ▼                   ▼
┌──────────┐        ┌────────────┐      ┌──────────┐
│  Course  │◀──────▶│ Enrollment │─────▶│ Payment  │
└──────────┘        └────────────┘      └──────────┘

┌──────────┐
│ AppUser  │  (Entidad independiente — gestiona autenticación)
└──────────┘
```

> El diagrama MER completo se encuentra en [`docs/MER_erp_iglesias.md`](docs/mer.html)

---

## 📁 Estructura del Repositorio

```
erp_iglesias/
├── 📄 README.md
├── 🐳 docker-compose.yml
│
├── 📂 docs/
│   ├── 📄 ADR_ERP_Iglesias.md          ← Documento ADR completo (10 decisiones)
│   ├── 📄 MER_erp_iglesias.md          ← Diagrama Modelo Entidad-Relación
│   │
│   ├── 📂 cambios/                     ← Documentación de los 5 cambios implementados
│   │   ├── CAMBIO-1_ADR-002_ChurchService.md
│   │   ├── CAMBIO-2_ADR-005_DTOs.md
│   │   ├── CAMBIO-3_ADR-001_ServiceLayer.md
│   │   ├── CAMBIO-4_ADR-007_AngularServices.md
│   │   └── CAMBIO-5_ADR-003_GlobalExceptionHandler.md
│   │
│   └── 📂 img/                         ← Evidencias visuales por ADR
│       ├── ADR-001-img/
│       ├── ADR-002-img/
│       ├── ADR-003-img/
│       ├── ADR-005-img/
│       └── ADR-007-img/
│
├── 📂 backend/
│   ├── src/main/java/com/iglesia/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   ├── exception/
│   │   └── security/
│   ├── Dockerfile
│   └── pom.xml
│
└── 📂 frontend/
    └── src/app/
        ├── services/
        ├── models/
        └── components/
```

---

## 📐 ADR — Architecture Decision Records

Se propusieron **10 decisiones arquitectónicas**, de las cuales **5 fueron implementadas**.

| ADR | Decisión | Patrón / Principio | Estado |
|-----|----------|-------------------|--------|
| [ADR-001](docs/cambios/CAMBIO-3_ADR-001_ServiceLayer.md) | Introducir Capa de Servicio | Service Layer + **SRP** | ✅ Implementado |
| [ADR-002](docs/cambios/CAMBIO-1_ADR-002_ChurchService.md) | Eliminar `requireChurch()` duplicado | Singleton + **DRY** | ✅ Implementado |
| [ADR-003](docs/cambios/CAMBIO-5_ADR-003_GlobalExceptionHandler.md) | Global Exception Handler | Facade + **OCP** | ✅ Implementado |
| ADR-004 | Strategy Pattern en pagos | Strategy + **OCP** | 🟡 Propuesto |
| [ADR-005](docs/cambios/CAMBIO-2_ADR-005_DTOs.md) | Separar DTOs de Controllers | DTO Pattern + **SRP** | ✅ Implementado |
| ADR-006 | Reorganizar paquetes por capas | Layered Architecture + **DIP** | 🟡 Propuesto |
| [ADR-007](docs/cambios/CAMBIO-4_ADR-007_AngularServices.md) | Servicios Angular por dominio | Service Layer + **SRP** | ✅ Implementado |
| ADR-008 | Modelos TypeScript dedicados | DTO Pattern + **ISP** | 🟡 Propuesto |
| ADR-009 | Interceptor HTTP global | Interceptor + **Facade** | 🟡 Propuesto |
| ADR-010 | Estado centralizado con Signals | State Pattern | 🟡 Propuesto |

>  Documento completo: [`docs/ADR_ERP_Iglesias.md`](docs/ADR_ERP_Iglesias.md)

---

## ✅ Cambios Implementados

### [Cambio 1 — ADR-002: ChurchService Singleton](docs/cambios/CAMBIO-1_ADR-002_ChurchService.md)
> Eliminación del método `requireChurch()` duplicado en 5 controladores. Se centralizó en un `ChurchService` gestionado como Singleton por Spring.

### [Cambio 2 — ADR-005: DTOs Separados](docs/cambios/CAMBIO-2_ADR-005_DTOs.md)
> Todos los records de transferencia de datos fueron movidos de las clases internas de los controladores a paquetes dedicados `dto/request/` y `dto/response/`.

### [Cambio 3 — ADR-001: Capa de Servicio](docs/cambios/CAMBIO-3_ADR-001_ServiceLayer.md)
> Se crearon 7 servicios (`EnrollmentService`, `OfferingService`, `PaymentService`, etc.) extrayendo la lógica de negocio de los controladores.

### [Cambio 4 — ADR-007: Servicios Angular](docs/cambios/CAMBIO-4_ADR-007_AngularServices.md)
> El `ApiService` monolítico del frontend fue dividido en 9 servicios especializados por dominio, con modelos TypeScript dedicados y URL centralizada en `environment.ts`.

### [Cambio 5 — ADR-003: GlobalExceptionHandler](docs/cambios/CAMBIO-5_ADR-003_GlobalExceptionHandler.md)
> Se implementó un manejador global de excepciones con `@RestControllerAdvice` que centraliza el manejo de errores y retorna respuestas JSON estandarizadas.

---

## Cómo Ejecutar el Proyecto

### Prerrequisitos
- Docker y Docker Compose instalados
- Puerto `8080` disponible (backend)
- Puerto `4200` disponible (frontend)

### Pasos

```bash
# 1. Clonar el repositorio
git clone <repository-url>
cd erp_iglesias

# 2. Levantar todos los servicios
docker-compose up -d

# 3. Verificar que los contenedores estén corriendo
docker-compose ps
```

### URLs de acceso

| Servicio | URL |
|---------|-----|
| Frontend Angular | http://localhost:4200 |
| Backend API | http://localhost:8080/api |

### Credenciales por defecto

| Rol | Email | Contraseña |
|-----|-------|-----------|
| ADMIN | admin@parroquia.com | Admin123! |

---

## 📊 Diagnóstico Arquitectónico — Hallazgos

| # | Problema identificado | Archivos afectados | Principio violado |
|---|----------------------|--------------------|------------------|
| 1 | Lógica de negocio en controllers | `EnrollmentController`, `OfferingController` | SRP |
| 2 | `requireChurch()` duplicado en 5 controllers | 5 archivos | DRY |
| 3 | Sin manejo global de excepciones | Todos los controllers | OCP |
| 4 | Referencia polimórfica sin FK (`referenceId`) | `Payment.java` | Integridad referencial |
| 5 | DTOs como clases internas de controllers | 8 controllers | SRP |
| 6 | Todas las clases en un solo paquete | 30+ archivos en `com.iglesia` | Layered Architecture |
| 7 | `ApiService` monolítico en Angular | `api.service.ts` | SRP |
| 8 | Sin tipado fuerte en el frontend | Todos los componentes | ISP |

---

*Parcial 1 — Arquitectura de Software · Marzo 2025*  
*Juan Sebastián Osorio Fierro · Karina Cantillo Plaza*