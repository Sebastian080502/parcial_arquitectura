# ERP Iglesias — Refactorización Arquitectónica

## Parcial 1 — Arquitectura de Software

Este repositorio contiene el desarrollo del **Parcial 1 de Arquitectura de Software**, enfocado en la **auditoría, documentación y refactorización incremental** del sistema **ERP Iglesias**.

El trabajo consiste en analizar la arquitectura actual del sistema, identificar problemas de diseño y proponer mejoras aplicando:

- Principios **SOLID**
- Buenas prácticas de **Clean Code**
- Documentación de decisiones mediante **ADR (Architecture Decision Records)**

---

## Integrantes

- Juan Sebastián Osorio Fierro
- Karina Cantillo Plaza

---

## Propósito del repositorio

Este repositorio tiene como objetivo evidenciar el proceso de refactorización de un sistema existente, documentando tanto las decisiones arquitectónicas como los cambios implementados sobre el código fuente.

La refactorización se realiza de manera incremental para evitar afectar el comportamiento funcional de la aplicación y para mantener la estabilidad del sistema.

---

## Objetivos

- Identificar problemas arquitectónicos y de diseño en el sistema actual.
- Proponer decisiones de arquitectura documentadas mediante ADR.
- Implementar cambios controlados aplicando principios SOLID.
- Mejorar la mantenibilidad, legibilidad y organización del código.
- Documentar evidencias técnicas mediante imágenes y commits.

---

## Tecnologías del sistema

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT

### Frontend

- Angular

### Infraestructura

- Docker
- Docker Compose

---

## Estructura del repositorio

```text
.
├── README.md
├── docs
│   ├── refactorizacion.md
│   ├── adr
│   │   ├── ADR-001-auth-service.md
│   │   ├── ADR-002-church-service.md
│   │   ├── ADR-003-person-service.md
│   │   ├── ADR-004-centralizar-require-church.md
│   │   ├── ADR-005-separacion-dtos.md
│   │   ├── ADR-006-global-exception-handler.md
│   │   ├── ADR-007-mappers.md
│   │   ├── ADR-008-api-routes.md
│   │   ├── ADR-009-factory-method-entidades.md
│   │   └── ADR-010-externalizacion-secretos.md
│   └── img
│       └── evidencias
├── backend
├── frontend
└── docker-compose.yml
```

Documentación principal

La documentación del proceso de refactorización se encuentra en:

docs/refactorizacion.md

Allí se documenta:

diagnóstico arquitectónico

aplicación de SOLID y Clean Code

modelo entidad relación

cambios implementados

evidencia visual antes y después

commits asociados

ADR

Las decisiones arquitectónicas se documentan por separado en:

docs/adr/

Se plantean 10 decisiones arquitectónicas, de las cuales 5 son implementadas durante el parcial.

Evidencias

Las evidencias visuales se almacenan en:

docs/img/evidencias/

Ejemplos de evidencias:

antes y después de controladores

creación de servicios

separación de DTOs

manejo global de excepciones

estructura refactorizada del proyecto

Cambios implementados

Los cambios implementados en el sistema son:

Introducción de AuthService

Introducción de ChurchService

Introducción de PersonService

Separación de DTOs

Implementación de GlobalExceptionHandler

Cada implementación debe registrarse con un commit independiente para usarlo también como evidencia del proceso.
