# Architecture Decision Record (ADR)

## ERP Iglesias — Sistema de Gestión Administrativa

---

> **Materia:** Arquitectura de Software — Primer Corte  
> **Equipo:** Karina Cantillo Plaza · Sebastian Osorio Fierro  
> **Repositorio:** `lanvargas94/erp_iglesias`  
> **Fecha:** Marzo 2025  
> **Deadline:** 09 de Marzo 2025 — 11:59 AM

---

## Tabla de Contenidos

- [1. Contexto General del Sistema](#1-contexto-general-del-sistema)
- [2. Stack Tecnológico Actual](#2-stack-tecnológico-actual)
- [3. Estructura del Proyecto](#3-estructura-del-proyecto)
- [4. Diagnóstico Arquitectónico](#4-diagnóstico-arquitectónico)
- [5. Diagrama MER](#5-diagrama-mer)
- [6. Decisiones Arquitectónicas (10 ADRs)](#6-decisiones-arquitectónicas)
  - [ADR-001: Service Layer Pattern](#adr-001-introducir-capa-de-servicio-service-layer-pattern)
  - [ADR-002: Singleton + DRY en requireChurch()](#adr-002-eliminar-código-duplicado-con-churchservice-singleton--dry)
  - [ADR-003: Global Exception Handler](#adr-003-global-exception-handler-con-controlleradvice)
  - [ADR-004: Strategy Pattern en Payment](#adr-004-resolver-referencia-polimórfica-con-strategy-pattern)
  - [ADR-005: DTO Pattern](#adr-005-separar-dtos-de-los-controladores-dto-pattern--srp)
  - [ADR-006: Layered Architecture](#adr-006-reorganizar-paquetes-por-capas-layered-architecture)
  - [ADR-007: Services por Dominio en Angular](#adr-007-separar-apiservice-en-servicios-por-dominio)
  - [ADR-008: Modelos TypeScript Dedicados](#adr-008-crear-modelos-dedicados-dto-pattern--tipado-fuerte)
  - [ADR-009: Interceptor HTTP Global](#adr-009-implementar-interceptor-http-para-manejo-global-de-errores)
  - [ADR-010: Estado Centralizado con Signals](#adr-010-introducir-estado-centralizado-con-signals)
- [7. Cambios Implementados (5 de 10)](#7-cambios-implementados-5-de-10)
- [8. Consecuencias Generales](#8-consecuencias-generales)

---

## 1. Contexto General del Sistema

**ERP Iglesias** es un sistema de planificación de recursos empresariales (ERP) diseñado para la gestión administrativa de iglesias. Permite administrar miembros (personas), cursos, inscripciones, ofrendas y pagos, con control de acceso basado en roles (`ADMIN` y `CLIENT`).

El sistema fue desarrollado como un monolito con arquitectura cliente-servidor: un backend REST en Java/Spring Boot y un frontend SPA en Angular, comunicados mediante JWT. Aunque el sistema es funcional, el análisis del código fuente revela múltiples oportunidades de mejora arquitectónica que se documentan en este ADR.

---

## 2. Stack Tecnológico Actual

| Capa                 | Tecnología                       | Versión                            | Rol                                        |
| -------------------- | -------------------------------- | ---------------------------------- | ------------------------------------------ |
| **Backend**          | Java + Spring Boot               | Java 17 / SB 3.2.3                 | API REST, lógica de negocio                |
| **Seguridad**        | Spring Security + JWT            | jjwt 0.11.5                        | Autenticación stateless con roles          |
| **Persistencia**     | Spring Data JPA + Hibernate      | Hibernate 6 (incluido en SB 3.2.3) | ORM, repositorios CRUD                     |
| **Validación**       | Spring Boot Validation (jakarta) | SB 3.2.3                           | Validación de DTOs con anotaciones         |
| **Base de datos**    | PostgreSQL                       | Latest (Docker)                    | Almacenamiento relacional                  |
| **Frontend**         | Angular + Angular Material       | Angular 17.3 / CDK 17              | SPA, interfaz de usuario                   |
| **Reactividad**      | RxJS                             | 7.8                                | Programación reactiva en Angular           |
| **Tipado**           | TypeScript                       | 5.4.2                              | Tipado estático en frontend                |
| **Infraestructura**  | Docker + Docker Compose          | Latest                             | Contenedores, orquestación local           |
| **Proxy**            | Nginx                            | Latest                             | Reverse proxy para Angular en producción   |
| **Build Backend**    | Maven + Spring Boot Maven Plugin | SB 3.2.3                           | Compilación y empaquetado                  |
| **Build Frontend**   | Angular CLI                      | 17.3.17                            | Compilación, build, servidor de desarrollo |
| **Testing Frontend** | Karma + Jasmine                  | Karma 6.4 / Jasmine 5.1            | Unit testing Angular                       |

> ⚠️ **Hallazgo:** No se encontraron pruebas unitarias en el backend. No existe ningún archivo de test en `src/test/java/`.

---

## 3. Estructura del Proyecto

```
erp_iglesias/
├── backend/
│   ├── src/main/
│   │   ├── java/com/iglesia/          ← ⚠️ TODAS las clases en un solo paquete
│   │   │   ├── AppUser.java
│   │   │   ├── AppUserRepository.java
│   │   │   ├── AuthController.java
│   │   │   ├── AuthUserDetailsService.java
│   │   │   ├── Church.java
│   │   │   ├── ChurchController.java
│   │   │   ├── ChurchRepository.java
│   │   │   ├── Course.java
│   │   │   ├── CourseController.java
│   │   │   ├── CourseRepository.java
│   │   │   ├── DashboardController.java
│   │   │   ├── DataInitializer.java
│   │   │   ├── Enrollment.java
│   │   │   ├── EnrollmentController.java
│   │   │   ├── EnrollmentRepository.java
│   │   │   ├── EnrollmentStatus.java
│   │   │   ├── IglesiaAdminApplication.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   ├── JwtService.java
│   │   │   ├── Offering.java
│   │   │   ├── OfferingController.java
│   │   │   ├── OfferingRepository.java
│   │   │   ├── OfferingStatus.java
│   │   │   ├── Payment.java
│   │   │   ├── PaymentController.java
│   │   │   ├── PaymentRepository.java
│   │   │   ├── PaymentStatus.java
│   │   │   ├── PaymentType.java
│   │   │   ├── Person.java
│   │   │   ├── PersonController.java
│   │   │   ├── PersonRepository.java
│   │   │   ├── SecurityConfig.java
│   │   │   ├── UserController.java
│   │   │   └── UserRole.java
│   │   └── resources/
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   └── src/app/
│       ├── api.service.ts             ← ⚠️ Servicio monolítico con todos los métodos HTTP
│       ├── auth.guard.ts
│       ├── auth.interceptor.ts        ← Solo agrega token, sin manejo de errores
│       ├── auth.service.ts
│       ├── app.config.ts
│       ├── app.routes.ts
│       ├── church.component.ts
│       ├── courses.component.ts
│       ├── dashboard.component.ts
│       ├── enrollments.component.ts
│       ├── login.component.ts
│       ├── offerings.component.ts
│       ├── payments.component.ts
│       ├── people.component.ts
│       └── users.component.ts
└── docker-compose.yml
```

---

## 4. Diagnóstico Arquitectónico

El análisis del código fuente revela los siguientes problemas arquitectónicos:

| #   | Problema                                                   | Archivos Afectados                                                                                          | Principio Violado      |
| --- | ---------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- | ---------------------- |
| 1   | Lógica de negocio mezclada en controladores                | `EnrollmentController.java`, `OfferingController.java`                                                      | SRP                    |
| 2   | Método `requireChurch()` duplicado en 5 controladores      | `CourseController`, `EnrollmentController`, `OfferingController`, `PersonController`, `DashboardController` | DRY                    |
| 3   | Sin capa de manejo global de excepciones                   | Todos los controllers                                                                                       | OCP                    |
| 4   | Referencia polimórfica sin FK formal (`referenceId`)       | `Payment.java`, `Enrollment.java`, `Offering.java`                                                          | Integridad referencial |
| 5   | DTOs definidos como clases internas de controllers         | `AuthController`, `EnrollmentController`, `PaymentController`, etc.                                         | SRP                    |
| 6   | Todas las clases en un solo paquete `com.iglesia`          | 30+ archivos                                                                                                | Layered Architecture   |
| 7   | `ApiService` monolítico en Angular con todos los endpoints | `api.service.ts`                                                                                            | SRP                    |
| 8   | Interfaces de dominio acopladas al servicio HTTP           | `api.service.ts`                                                                                            | Interface Segregation  |
| 9   | Interceptor sin manejo de errores HTTP                     | `auth.interceptor.ts`                                                                                       | Facade                 |
| 10  | Estado local por componente, sin reactividad compartida    | Todos los componentes                                                                                       | State Pattern          |

---

## 5. Diagrama MER

El modelo entidad-relación fue inferido directamente desde las entidades JPA del backend.

```
┌─────────────────────────────────────────────────────────────────────┐
│                          DIAGRAMA MER                               │
│                         ERP Iglesias                                │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐
│   AppUser    │  (tabla: users)
│──────────────│
│ PK id        │  ← Entidad independiente, gestiona autenticación
│ UK email     │
│    passHash  │
│    role      │  ENUM: ADMIN, CLIENT
│    active    │
│    createdAt │
└──────────────┘

                        ┌──────────────────┐
                        │     Church        │  (tabla: churches)
                        │──────────────────│
                        │ PK id            │
                        │    name  NOT NULL│
                        │    address       │
                        │    createdAt     │
                        └────────┬─────────┘
                                 │ 1
                    ┌────────────┴────────────┐
                    │ N                       │ N
           ┌────────┴──────┐        ┌─────────┴────────┐
           │    Person      │        │     Course        │
           │  (tabla:people)│        │  (tabla: courses) │
           │───────────────│        │──────────────────│
           │ PK id         │        │ PK id            │
           │ FK church_id  │        │ FK church_id     │
           │    firstName  │        │    name NOT NULL │
           │    lastName   │        │    description   │
           │    document   │        │    price DEC(12,2│
           │    phone      │        │    active        │
           │    email      │        │    createdAt     │
           │    createdAt  │        └────────┬─────────┘
           └───────┬───────┘                 │ 1
                   │ 1                       │
        ┌──────────┴───────────┐             │ N
        │ N                   │ N            │
        │              ┌──────┴──────────────┤
        │              │    Enrollment        │  (tabla: enrollments)
        │              │─────────────────────│
        │              │ PK id               │
        │              │ FK person_id        │  ←─┐
        │              │ FK course_id        │    │  N:M resuelta
        │              │    status  ENUM     │    │  por Enrollment
        │              │ ⚠️  paymentId LONG  │  ←─┘
        │              │    createdAt        │
        │              └──────────┬──────────┘
        │                         │ ⚠️ ref. débil
        │                         │
        │  N                      │
┌───────┴──────┐                  │   ┌──────────────────┐
│   Offering   │  (tabla: offerings)  │    Payment        │
│──────────────│                  │   │  (tabla: payments)│
│ PK id        │                  │   │──────────────────│
│ FK person_id │                  │   │ PK id            │
│    amount    │                  └──►│ ⚠️ referenceId   │◄──┐
│    concept   │                      │    type  ENUM    │   │
│    status    │  ENUM                │    status ENUM   │   │
│ ⚠️ paymentId │──────────────────────►│    amount        │   │
│    createdAt │  ⚠️ ref. débil       │    attempts      │   │
└──────────────┘                      │    createdAt     │   │
                                      │    updatedAt     │   │
                                      └──────────────────┘   │
                                                              │
  ⚠️ ANTI-PATRÓN: Payment.referenceId es un Long genérico ───┘
     que apunta a Enrollment.id O Offering.id según
     Payment.type, sin FK formal en la base de datos.

Enumeraciones:
  UserRole:         ADMIN | CLIENT
  EnrollmentStatus: PENDIENTE | ACTIVO | COMPLETADO | CANCELADO | PAGADA
  OfferingStatus:   PENDIENTE | COMPLETADO | CANCELADO | REGISTRADA
  PaymentType:      INSCRIPCION_CURSO | OFRENDA
  PaymentStatus:    INICIADO | PROCESANDO | CONFIRMADO | FALLIDO
```

### Relaciones

| Entidad A  | Cardinalidad | Entidad B  | Tipo FK                    |
| ---------- | ------------ | ---------- | -------------------------- |
| Church     | 1 : N        | Person     | `@ManyToOne` formal        |
| Church     | 1 : N        | Course     | `@ManyToOne` formal        |
| Person     | N : M        | Course     | Resuelta por `Enrollment`  |
| Person     | 1 : N        | Enrollment | `@ManyToOne` formal        |
| Course     | 1 : N        | Enrollment | `@ManyToOne` formal        |
| Person     | 1 : N        | Offering   | `@ManyToOne` formal        |
| Enrollment | 1 : 1        | Payment    | ⚠️ `Long paymentId` sin FK |
| Offering   | 1 : 1        | Payment    | ⚠️ `Long paymentId` sin FK |

---

## 6. Decisiones Arquitectónicas

---

### ADR-001: Introducir Capa de Servicio (Service Layer Pattern)

| Campo                  | Detalle                                                                                                      |
| ---------------------- | ------------------------------------------------------------------------------------------------------------ |
| **Estado**             | 🟡 Propuesto                                                                                                 |
| **Patrón / Principio** | Service Layer + **SRP** (Single Responsibility Principle)                                                    |
| **Archivos afectados** | `EnrollmentController.java`, `OfferingController.java`, `PaymentController.java`, `DashboardController.java` |

#### Contexto

Los controladores actuales mezclan múltiples responsabilidades en un solo método. Por ejemplo, `EnrollmentController.create()` realiza en secuencia:

1. Obtención de la iglesia (`requireChurch()`)
2. Búsqueda y validación de `Person` por ID
3. Búsqueda y validación de `Course` por ID
4. Validación de pertenencia a la misma iglesia
5. Creación y persistencia de `Enrollment`
6. Creación y persistencia de `Payment`
7. Actualización de la referencia cruzada `enrollment.paymentId`
8. Construcción y retorno de `EnrollmentResponse`

Esto viola el **Principio de Responsabilidad Única (SRP)**: el controlador tiene múltiples razones para cambiar. Además, la lógica de negocio no es testeable sin levantar el contexto HTTP completo.

#### Decisión

Crear servicios específicos anotados con `@Service` y `@Transactional`:

- `EnrollmentService` — lógica de inscripciones
- `OfferingService` — lógica de ofrendas
- `PaymentService` — lógica de pagos
- `ChurchService` — lógica de iglesia (ver ADR-002)

Los controladores se limitarán a: recibir la petición HTTP → delegar al servicio → retornar la respuesta.

#### Ejemplo

**Antes** (`EnrollmentController.java`):

```java
@PostMapping
public EnrollmentResponse create(@RequestBody EnrollmentRequest req) {
    Church church = requireChurch();                                          // lógica de negocio
    Person person = personRepository.findById(req.personId())                // acceso a datos
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "..."));
    Course course = courseRepository.findById(req.courseId())                // acceso a datos
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "..."));
    if (!person.getChurch().getId().equals(church.getId()) ||               // validación de negocio
        !course.getChurch().getId().equals(church.getId())) {
        throw new ResponseStatusException(BAD_REQUEST, "...");
    }
    Enrollment enrollment = new Enrollment();                                // construcción de entidad
    enrollment.setPerson(person);
    enrollment.setCourse(course);
    enrollment.setStatus(EnrollmentStatus.PENDIENTE);
    enrollmentRepository.save(enrollment);
    Payment payment = new Payment();                                         // creación de pago
    payment.setType(PaymentType.INSCRIPCION_CURSO);
    payment.setAmount(course.getPrice());
    payment.setReferenceId(enrollment.getId());
    paymentRepository.save(payment);
    enrollment.setPaymentId(payment.getId());
    enrollmentRepository.save(enrollment);
    return EnrollmentResponse.from(enrollment, payment);
}
```

**Después** (`EnrollmentController.java`):

```java
@PostMapping
public ResponseEntity<EnrollmentResponse> create(
        @Valid @RequestBody EnrollmentRequest req) {
    return ResponseEntity.ok(enrollmentService.create(req));  // solo delega
}
```

```java
// EnrollmentService.java
@Service
@Transactional
public class EnrollmentService {
    public EnrollmentResponse create(EnrollmentRequest req) {
        Church church = churchService.getRequiredChurch();
        Person person = personRepository.findById(req.personId())
            .orElseThrow(PersonNotFoundException::new);
        Course course = courseRepository.findById(req.courseId())
            .orElseThrow(CourseNotFoundException::new);
        validateSameChurch(person, course, church);
        Enrollment enrollment = buildEnrollment(person, course);
        enrollmentRepository.save(enrollment);
        Payment payment = paymentService.createFor(enrollment);
        enrollment.setPaymentId(payment.getId());
        return EnrollmentResponse.from(enrollment, payment);
    }
}
```

#### Consecuencias

| ✅ Beneficios                                                   | ⚠️ Trade-offs                                      |
| --------------------------------------------------------------- | -------------------------------------------------- |
| Cumple SRP: cada clase tiene una sola razón para cambiar        | Aumenta el número de clases (4 servicios nuevos)   |
| Lógica de negocio testeable sin contexto HTTP                   | Requiere refactorizar los controladores existentes |
| Permite reutilizar lógica entre controladores                   | El equipo debe entender la nueva estructura        |
| `@Transactional` garantiza atomicidad en operaciones compuestas | —                                                  |

---

### ADR-002: Eliminar Código Duplicado con ChurchService (Singleton + DRY)

| Campo                  | Detalle                                                                                                                              |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| **Estado**             | 🟡 Propuesto                                                                                                                         |
| **Patrón / Principio** | Singleton (Spring-managed) + **DRY** (Don't Repeat Yourself)                                                                         |
| **Archivos afectados** | `CourseController.java`, `EnrollmentController.java`, `OfferingController.java`, `PersonController.java`, `DashboardController.java` |

#### Contexto

El siguiente método aparece **idéntico** en los 5 controladores mencionados:

```java
private Church requireChurch() {
    return churchRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
}
```

Si la lógica de obtención de la iglesia cambia (ej. búsqueda por ID en lugar de `findAll().findFirst()`), hay que modificar 5 archivos. Esto viola el principio **DRY** y aumenta el riesgo de inconsistencias.

#### Decisión

Crear `ChurchService` con el método `getRequiredChurch()`. Al ser un bean de Spring anotado con `@Service`, el contenedor de Spring lo gestiona como **Singleton** (una sola instancia compartida entre todos los controladores que lo inyecten). El método lanzará `ChurchNotFoundException` en lugar de `ResponseStatusException` para desacoplar la capa de negocio de la capa HTTP.

#### Ejemplo

**Antes** (repetido en 5 controladores):

```java
// CourseController.java, EnrollmentController.java,
// OfferingController.java, PersonController.java, DashboardController.java
private Church requireChurch() {
    return churchRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "Debe registrar una iglesia primero"));
}
```

**Después** (`ChurchService.java`):

```java
@Service  // Spring gestiona esta clase como Singleton
public class ChurchService {
    private final ChurchRepository churchRepository;

    public ChurchService(ChurchRepository churchRepository) {
        this.churchRepository = churchRepository;
    }

    public Church getRequiredChurch() {
        return churchRepository.findAll().stream().findFirst()
            .orElseThrow(ChurchNotFoundException::new);  // excepción de dominio
    }
}
```

```java
// En cualquier controlador o servicio:
Church church = churchService.getRequiredChurch();  // un solo punto de verdad
```

#### Consecuencias

| ✅ Beneficios                                               | ⚠️ Trade-offs                                                     |
| ----------------------------------------------------------- | ----------------------------------------------------------------- |
| Elimina duplicación en 5 archivos (principio DRY)           | Pequeño refactor en 5 controladores para inyectar `ChurchService` |
| Singleton de Spring: thread-safe, una sola instancia        | Crear la clase `ChurchNotFoundException`                          |
| Cambio en un único lugar si cambia la lógica                | —                                                                 |
| Desacopla la capa HTTP de la lógica de obtención de iglesia | —                                                                 |

---

### ADR-003: Global Exception Handler con @ControllerAdvice

| Campo                  | Detalle                                                                             |
| ---------------------- | ----------------------------------------------------------------------------------- |
| **Estado**             | 🟡 Propuesto                                                                        |
| **Patrón / Principio** | Facade Pattern + **OCP** (Open/Closed Principle)                                    |
| **Archivos afectados** | Todos los controllers + nuevos: `GlobalExceptionHandler.java`, `ErrorResponse.java` |

#### Contexto

Actualmente cada controlador lanza `ResponseStatusException` directamente con mensajes hardcodeados. Esto genera tres problemas:

1. **Sin formato uniforme**: el cliente recibe respuestas de error inconsistentes.
2. **Sin logging centralizado**: los errores no se registran de forma sistemática.
3. **Viola OCP**: si se quiere cambiar el formato de respuesta de error, hay que modificar todos los controladores.

#### Decisión

Implementar `GlobalExceptionHandler` con `@ControllerAdvice` que:

- Capture todas las excepciones de dominio (`ChurchNotFoundException`, `PersonNotFoundException`, `CourseNotFoundException`, `BusinessRuleException`)
- Retorne un `ErrorResponse` estandarizado con timestamp, status, mensaje y path
- Centralice el logging de errores
- Sea extensible: agregar un nuevo tipo de excepción = agregar un método `@ExceptionHandler`, sin modificar código existente (OCP)

#### Ejemplo

```java
// ErrorResponse.java
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}
```

```java
// GlobalExceptionHandler.java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ChurchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChurchNotFound(
            ChurchNotFoundException ex, HttpServletRequest request) {
        log.warn("Church not found: {}", ex.getMessage());
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(
                LocalDateTime.now(), 400, "Bad Request",
                "Debe registrar una iglesia primero", request.getRequestURI()));
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFound(
            PersonNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(
                LocalDateTime.now(), 404, "Not Found",
                "Persona no encontrada", request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)  // fallback
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(
                LocalDateTime.now(), 500, "Internal Server Error",
                "Error inesperado en el servidor", request.getRequestURI()));
    }
}
```

**Respuesta JSON estandarizada:**

```json
{
  "timestamp": "2025-03-05T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Persona no encontrada",
  "path": "/api/enrollments"
}
```

#### Consecuencias

| ✅ Beneficios                                                         | ⚠️ Trade-offs                                        |
| --------------------------------------------------------------------- | ---------------------------------------------------- |
| API retorna errores con formato JSON consistente                      | Requiere crear excepciones de dominio personalizadas |
| Logging centralizado de todas las excepciones                         | Curva de aprendizaje para el equipo                  |
| OCP: agregar nueva excepción = nuevo método, sin tocar los existentes | —                                                    |
| Los controladores quedan más limpios (sin manejo de errores HTTP)     | —                                                    |

---

### ADR-004: Resolver Referencia Polimórfica con Strategy Pattern

| Campo                  | Detalle                                                                      |
| ---------------------- | ---------------------------------------------------------------------------- |
| **Estado**             | 🟡 Propuesto                                                                 |
| **Patrón / Principio** | Strategy Pattern + **OCP** + **DIP** (Dependency Inversion)                  |
| **Archivos afectados** | `PaymentController.java`, `Payment.java`, `Enrollment.java`, `Offering.java` |

#### Contexto

En `PaymentController.confirm()` existe este bloque condicional:

```java
if (payment.getType() == PaymentType.INSCRIPCION_CURSO) {
    Enrollment enrollment = enrollmentRepository
        .findById(payment.getReferenceId()).orElseThrow(...);
    enrollment.setStatus(EnrollmentStatus.PAGADA);
    enrollmentRepository.save(enrollment);
} else if (payment.getType() == PaymentType.OFRENDA) {
    Offering offering = offeringRepository
        .findById(payment.getReferenceId()).orElseThrow(...);
    offering.setStatus(OfferingStatus.REGISTRADA);
    offeringRepository.save(offering);
}
```

Problemas identificados:

1. **Viola OCP**: agregar un nuevo tipo de pago (ej. `DONACION`) obliga a modificar este método.
2. **Anti-patrón de referencia polimórfica**: `Payment.referenceId` es un `Long` genérico sin `@ManyToOne`, la base de datos no puede garantizar integridad referencial.
3. **Acoplamiento alto**: `PaymentController` conoce directamente los repositorios de `Enrollment` y `Offering`.

#### Decisión

Aplicar el **patrón Strategy**. Cada tipo de pago tendrá su propia estrategia que encapsula la lógica de confirmación. `PaymentService` mantiene un mapa de estrategias inyectado por Spring y delega sin condicionales.

#### Ejemplo

```java
// Interfaz de la estrategia
public interface PaymentConfirmStrategy {
    void confirm(Payment payment);
    PaymentType getType();
}
```

```java
// Estrategia para inscripciones
@Component
public class EnrollmentPaymentStrategy implements PaymentConfirmStrategy {
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void confirm(Payment payment) {
        Enrollment enrollment = enrollmentRepository
            .findById(payment.getReferenceId())
            .orElseThrow(EnrollmentNotFoundException::new);
        enrollment.setStatus(EnrollmentStatus.PAGADA);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public PaymentType getType() {
        return PaymentType.INSCRIPCION_CURSO;
    }
}
```

```java
// Estrategia para ofrendas
@Component
public class OfferingPaymentStrategy implements PaymentConfirmStrategy {
    @Override
    public void confirm(Payment payment) { /* actualiza OfferingStatus */ }
    @Override
    public PaymentType getType() { return PaymentType.OFRENDA; }
}
```

```java
// PaymentService — sin if/else, extensible
@Service
public class PaymentService {
    private final Map<PaymentType, PaymentConfirmStrategy> strategies;

    // Spring inyecta automáticamente todas las implementaciones
    public PaymentService(List<PaymentConfirmStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(PaymentConfirmStrategy::getType,
                                      Function.identity()));
    }

    public Payment confirm(Long paymentId) {
        Payment payment = findOrThrow(paymentId);
        payment.setStatus(PaymentStatus.CONFIRMADO);
        strategies.get(payment.getType()).confirm(payment); // delega sin if/else
        return paymentRepository.save(payment);
    }
}
```

#### Consecuencias

| ✅ Beneficios                                                                  | ⚠️ Trade-offs                                      |
| ------------------------------------------------------------------------------ | -------------------------------------------------- |
| Elimina el `if/else` frágil en confirmación de pagos                           | Aumenta el número de clases (una por tipo de pago) |
| OCP: agregar nuevo tipo de pago = nueva clase Strategy                         | Requiere un mapa de estrategias                    |
| Cada estrategia es testeable de forma independiente                            | El equipo debe conocer el patrón Strategy          |
| DIP: `PaymentService` depende de la interfaz, no de implementaciones concretas | —                                                  |

---

### ADR-005: Separar DTOs de los Controladores (DTO Pattern + SRP)

| Campo                  | Detalle                                                                                                                                                                                                   |
| ---------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Estado**             | 🟡 Propuesto                                                                                                                                                                                              |
| **Patrón / Principio** | DTO Pattern + **SRP**                                                                                                                                                                                     |
| **Archivos afectados** | `AuthController.java`, `EnrollmentController.java`, `OfferingController.java`, `PaymentController.java`, `CourseController.java`, `PersonController.java`, `UserController.java`, `ChurchController.java` |

#### Contexto

Los records de transferencia de datos (`LoginRequest`, `LoginResponse`, `EnrollmentRequest`, `EnrollmentResponse`, `PaymentResponse`, etc.) están definidos como **clases internas** de los controladores. Esto:

- Acopla la representación de datos con el controlador HTTP
- Impide reutilizar los DTOs desde los servicios o entre controladores
- Viola SRP: el controlador tiene responsabilidad sobre la estructura de los datos Y sobre el manejo HTTP

#### Decisión

Mover todos los DTOs a un paquete dedicado `com.iglesia.dto` con subpaquetes:

- `com.iglesia.dto.request` — objetos de entrada con validaciones
- `com.iglesia.dto.response` — objetos de salida

#### Ejemplo

**Antes** (DTO acoplado al controlador):

```java
public class EnrollmentController {
    // ... 60 líneas de lógica ...

    public record EnrollmentRequest(   // ← DTO dentro del controller
        @NotNull Long personId,
        @NotNull Long courseId
    ) {}

    public record EnrollmentResponse(  // ← DTO dentro del controller
        Long id, String personName, String courseName,
        String status, Long paymentId, String paymentStatus
    ) {}
}
```

**Después** (DTOs en paquete propio):

```java
// com/iglesia/dto/request/EnrollmentRequest.java
package com.iglesia.dto.request;

public record EnrollmentRequest(
    @NotNull Long personId,
    @NotNull Long courseId
) {}
```

```java
// com/iglesia/dto/response/EnrollmentResponse.java
package com.iglesia.dto.response;

public record EnrollmentResponse(
    Long id,
    Long personId,
    String personName,
    Long courseId,
    String courseName,
    String status,
    Long paymentId,
    String paymentStatus
) {}
```

#### Consecuencias

| ✅ Beneficios                                                            | ⚠️ Trade-offs                                |
| ------------------------------------------------------------------------ | -------------------------------------------- |
| DTOs reutilizables entre controllers y services                          | Refactor de imports en todos los controllers |
| Controladores más pequeños y enfocados en HTTP                           | —                                            |
| Validaciones centralizadas y visibles                                    | —                                            |
| SRP: el controller no tiene responsabilidad sobre la estructura de datos | —                                            |

---

### ADR-006: Reorganizar Paquetes por Capas (Layered Architecture)

| Campo                  | Detalle                                               |
| ---------------------- | ----------------------------------------------------- |
| **Estado**             | 🟡 Propuesto                                          |
| **Patrón / Principio** | Layered Architecture + **DIP** (Dependency Inversion) |
| **Archivos afectados** | Todos los archivos de `com.iglesia` (30+)             |

#### Contexto

Las 30+ clases del backend conviven en el paquete `com.iglesia` sin ninguna separación por capas ni dominio. Esto genera:

- Acoplamiento implícito entre clases de diferente responsabilidad
- Dificultad para navegar el proyecto
- Imposibilidad de aplicar restricciones de acceso entre capas

#### Decisión

Reorganizar en paquetes por capa con dependencias **unidireccionales**:

```
controller → service → repository
                    → entity
                    → dto
security (independiente)
exception (independiente)
```

#### Estructura Propuesta

```
com.iglesia/
├── controller/
│   ├── AuthController.java
│   ├── ChurchController.java
│   ├── CourseController.java
│   ├── DashboardController.java
│   ├── EnrollmentController.java
│   ├── OfferingController.java
│   ├── PaymentController.java
│   ├── PersonController.java
│   └── UserController.java
├── service/
│   ├── ChurchService.java
│   ├── EnrollmentService.java
│   ├── OfferingService.java
│   ├── PaymentService.java
│   └── strategy/
│       ├── PaymentConfirmStrategy.java
│       ├── EnrollmentPaymentStrategy.java
│       └── OfferingPaymentStrategy.java
├── repository/
│   ├── AppUserRepository.java
│   ├── ChurchRepository.java
│   ├── CourseRepository.java
│   ├── EnrollmentRepository.java
│   ├── OfferingRepository.java
│   ├── PaymentRepository.java
│   └── PersonRepository.java
├── entity/
│   ├── AppUser.java
│   ├── Church.java
│   ├── Course.java
│   ├── Enrollment.java
│   ├── Offering.java
│   ├── Payment.java
│   └── Person.java
├── dto/
│   ├── request/
│   │   ├── EnrollmentRequest.java
│   │   ├── LoginRequest.java
│   │   └── ...
│   └── response/
│       ├── EnrollmentResponse.java
│       ├── PaymentResponse.java
│       └── ...
├── security/
│   ├── AuthUserDetailsService.java
│   ├── JwtAuthFilter.java
│   ├── JwtService.java
│   └── SecurityConfig.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ChurchNotFoundException.java
│   ├── PersonNotFoundException.java
│   └── ErrorResponse.java
└── IglesiaAdminApplication.java
```

#### Consecuencias

| ✅ Beneficios                                                    | ⚠️ Trade-offs                                    |
| ---------------------------------------------------------------- | ------------------------------------------------ |
| Arquitectura visible desde la estructura de paquetes             | Refactor de imports en todas las clases          |
| Facilita el onboarding de nuevos desarrolladores                 | Puede automatizarse con el IDE (Refactor → Move) |
| Reduce acoplamiento implícito entre capas                        | —                                                |
| Permite aplicar restricciones de acceso entre capas en el futuro | —                                                |

---

### ADR-007: Separar ApiService en Servicios por Dominio

| Campo                  | Detalle                                           |
| ---------------------- | ------------------------------------------------- |
| **Estado**             | 🟡 Propuesto                                      |
| **Patrón / Principio** | Service Layer + **SRP** + **DRY**                 |
| **Archivos afectados** | `api.service.ts` → dividir en múltiples servicios |

#### Contexto

El archivo `api.service.ts` es un servicio monolítico que contiene **todos** los métodos HTTP del frontend (login, church, people, courses, enrollments, offerings, payments, dashboard), así como todas las interfaces de dominio. Viola SRP y dificulta el mantenimiento. Si se agrega un nuevo módulo, este archivo crece indefinidamente.

#### Decisión

Crear servicios específicos por dominio en `src/app/services/`. La URL base se definirá en `environment.ts`. Los componentes inyectarán solo los servicios que realmente necesiten.

#### Ejemplo

**Antes** (`api.service.ts` — monolítico):

```typescript
@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = 'http://localhost:8080/api';  // ← URL hardcodeada

  // 20+ métodos de todos los dominios mezclados
  login(email: string, password: string): Observable<any> { ... }
  getChurch(): Observable<any> { ... }
  getPeople(): Observable<any> { ... }
  getCourses(): Observable<any> { ... }
  createEnrollment(payload: any): Observable<any> { ... }
  // ...
}
```

**Después** (`courses.service.ts`):

```typescript
// src/app/services/courses.service.ts
@Injectable({ providedIn: "root" })
export class CoursesService {
  private apiUrl = `${environment.apiUrl}/courses`;

  constructor(private http: HttpClient) {}

  list(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  create(payload: CoursePayload): Observable<Course> {
    return this.http.post<Course>(this.apiUrl, payload);
  }
}
```

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: "http://localhost:8080/api", // un solo lugar para cambiar
};
```

**Servicios a crear:**

| Servicio             | Responsabilidad                 |
| -------------------- | ------------------------------- |
| `AuthService`        | Login, estado de autenticación  |
| `ChurchService`      | CRUD de iglesia                 |
| `PeopleService`      | CRUD de personas                |
| `CoursesService`     | CRUD de cursos                  |
| `EnrollmentsService` | CRUD de inscripciones           |
| `OfferingsService`   | CRUD de ofrendas                |
| `PaymentsService`    | Pagos, confirmación, reintentos |
| `DashboardService`   | Métricas del dashboard          |

#### Consecuencias

| ✅ Beneficios                                             | ⚠️ Trade-offs                                           |
| --------------------------------------------------------- | ------------------------------------------------------- |
| Un solo lugar para cambiar la URL base (`environment.ts`) | Requiere crear ~8 archivos de servicio                  |
| Componentes más simples: inyectan solo lo que necesitan   | Refactorizar componentes para usar los nuevos servicios |
| Facilita el testing unitario de cada servicio             | —                                                       |
| SRP: cada servicio tiene una única razón para cambiar     | —                                                       |

---

### ADR-008: Crear Modelos Dedicados (DTO Pattern + Tipado Fuerte)

| Campo                  | Detalle                                                          |
| ---------------------- | ---------------------------------------------------------------- |
| **Estado**             | 🟡 Propuesto                                                     |
| **Patrón / Principio** | DTO Pattern + **Interface Segregation Principle (ISP)**          |
| **Archivos afectados** | `api.service.ts` (interfaces definidas allí) → `src/app/models/` |

#### Contexto

Las interfaces que definen las entidades (`Course`, `Person`, `Enrollment`, `Offering`, `Payment`, `Church`, `User`, `Dashboard`) están dentro de `api.service.ts`. Esto:

- Acopla la definición de los modelos con el servicio HTTP
- Impide importar interfaces sin importar el servicio completo
- Viola ISP: un componente que solo necesita la interfaz `Course` no debería depender de `ApiService`

#### Decisión

Crear `src/app/models/` con una interfaz por entidad y un barrel (`index.ts`) para importaciones limpias. Configurar alias en `tsconfig.json` para imports absolutos.

#### Ejemplo

**Antes** (interfaz acoplada al servicio):

```typescript
// api.service.ts
export interface Course {
  id: number;
  name: string;
  // ...
}
// Componente debe importar desde el servicio:
import { Course } from "./api.service";
```

**Después** (modelo independiente):

```typescript
// src/app/models/course.model.ts
export interface Course {
  id: number;
  name: string;
  description: string;
  price: number;
  active: boolean;
}

export interface CoursePayload {
  name: string;
  description?: string;
  price: number;
}
```

```typescript
// src/app/models/index.ts (barrel)
export * from "./course.model";
export * from "./person.model";
export * from "./enrollment.model";
export * from "./offering.model";
export * from "./payment.model";
export * from "./church.model";
export * from "./auth.model";
```

```json
// tsconfig.json — alias para imports limpios
{
  "compilerOptions": {
    "paths": {
      "@models": ["src/app/models/index.ts"]
    }
  }
}
```

```typescript
// Uso en cualquier componente o servicio:
import { Course, Person } from "@models";
```

#### Consecuencias

| ✅ Beneficios                                        | ⚠️ Trade-offs                                     |
| ---------------------------------------------------- | ------------------------------------------------- |
| Errores de tipo detectados en compilación            | Requiere actualizar imports en todos los archivos |
| Autocompletado preciso en el IDE                     | Mantener sincronía con los DTOs del backend       |
| ISP: importar solo las interfaces necesarias         | —                                                 |
| Facilita la sincronización con cambios en el backend | —                                                 |

---

### ADR-009: Implementar Interceptor HTTP para Manejo Global de Errores

| Campo                  | Detalle                          |
| ---------------------- | -------------------------------- |
| **Estado**             | 🟡 Propuesto                     |
| **Patrón / Principio** | Interceptor Pattern + **Facade** |
| **Archivos afectados** | `auth.interceptor.ts`            |

#### Contexto

El `auth.interceptor.ts` actualmente solo agrega el token JWT al header. No hay manejo centralizado de errores HTTP. Cada componente maneja errores de forma individual, lo que duplica código y puede llevar a inconsistencias. En particular, no se maneja globalmente el caso `401 Unauthorized` (sesión expirada).

#### Decisión

Extender el interceptor para capturar errores con `catchError` de RxJS:

- **401**: limpiar sesión y redirigir al login automáticamente
- **4xx/5xx**: mostrar notificación con `MatSnackBar` usando el mensaje del backend o uno genérico

#### Ejemplo

**Antes** (`auth.interceptor.ts` — solo token):

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token;
  const clonedReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;
  return next(clonedReq); // ← sin manejo de errores
};
```

**Después** (`auth.interceptor.ts` — token + errores):

```typescript
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const snackBar = inject(MatSnackBar);

  const token = auth.token;
  const clonedReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(clonedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        auth.clear();
        router.navigate(["/login"]);
      } else {
        const message = error.error?.message || "Ocurrió un error inesperado";
        snackBar.open(message, "Cerrar", { duration: 5000 });
      }
      return throwError(() => error);
    }),
  );
};
```

#### Consecuencias

| ✅ Beneficios                                                   | ⚠️ Trade-offs                                         |
| --------------------------------------------------------------- | ----------------------------------------------------- |
| Manejo centralizado de errores HTTP (Facade sobre la capa HTTP) | Interceptor tiene más responsabilidades               |
| Redirección automática al login en sesiones expiradas           | Inyectar `Router` y `MatSnackBar` en el interceptor   |
| Reduce código de manejo de errores en los componentes           | Cuidado con peticiones que no deben ser interceptadas |
| Experiencia de usuario consistente ante errores                 | —                                                     |

---

### ADR-010: Introducir Estado Centralizado con Signals (State Pattern)

| Campo                  | Detalle                                   |
| ---------------------- | ----------------------------------------- |
| **Estado**             | 🟡 Propuesto                              |
| **Patrón / Principio** | State Pattern (con Signals de Angular 17) |
| **Archivos afectados** | Todos los componentes Angular             |

#### Contexto

Actualmente cada componente Angular maneja su propio estado con variables locales y recarga los datos manualmente al navegar o al realizar acciones. Esto genera:

- **Inconsistencia**: si dos componentes necesitan los mismos datos (ej. lista de personas), cada uno hace su propia petición HTTP.
- **Recarga manual**: después de crear un curso, el componente recarga toda la lista.
- **Sin reactividad**: si el estado cambia en un componente, otros no se enteran.

#### Decisión

Utilizar **Signals de Angular 17** para crear stores simples por dominio. Cada store mantiene la lista de entidades y expone métodos para mutarla. Los componentes leen los signals y llaman métodos del store, logrando reactividad sin necesidad de librerías externas como NgRx.

#### Ejemplo

```typescript
// src/app/stores/courses.store.ts
@Injectable({ providedIn: "root" })
export class CoursesStore {
  private coursesSignal = signal<Course[]>([]);

  // Solo lectura hacia afuera
  readonly courses = this.coursesSignal.asReadonly();

  // Signals derivados
  readonly activeCourses = computed(() =>
    this.coursesSignal().filter((c) => c.active),
  );

  constructor(private coursesService: CoursesService) {
    this.load();
  }

  load(): void {
    this.coursesService
      .list()
      .subscribe((courses) => this.coursesSignal.set(courses));
  }

  add(course: Course): void {
    this.coursesSignal.update((list) => [...list, course]);
  }

  remove(id: number): void {
    this.coursesSignal.update((list) => list.filter((c) => c.id !== id));
  }
}
```

```typescript
// courses.component.ts — usa el store
@Component({
  template: `
    @for (course of store.courses(); track course.id) {
      <mat-card>{{ course.name }}</mat-card>
    }
  `,
})
export class CoursesComponent {
  store = inject(CoursesStore);

  createCourse(payload: CoursePayload): void {
    this.coursesService
      .create(payload)
      .subscribe((newCourse) => this.store.add(newCourse)); // ← actualiza estado reactivamente
  }
}
```

#### Consecuencias

| ✅ Beneficios                                         | ⚠️ Trade-offs                                        |
| ----------------------------------------------------- | ---------------------------------------------------- |
| Estado reactivo: la UI se actualiza automáticamente   | Curva de aprendizaje de Signals de Angular 17        |
| Evita peticiones HTTP redundantes                     | Puede aumentar complejidad si se abusa               |
| Comunicación entre componentes sin `@Input`/`@Output` | Requiere refactorizar componentes para usar el store |
| Compatible con Angular 17 (ya usado en el proyecto)   | —                                                    |
| No requiere librerías externas (NgRx, Akita)          | —                                                    |

---

## 7. Cambios Implementados (5 de 10)

De los 10 ADRs propuestos, se implementaron los siguientes 5 cambios en el código fuente:

---

### ✅ Cambio 1 — ADR-001: EnrollmentService (Service Layer)

**Archivos modificados:**

- ✏️ `EnrollmentController.java` — simplificado a delegación
- ✨ `EnrollmentService.java` — nueva clase con lógica de negocio

**Evidencia funcional:**  
Se realizó una petición `POST /api/enrollments` con Postman antes y después del cambio. La respuesta JSON es idéntica, el comportamiento no cambió. Los logs muestran que `EnrollmentService.create()` es invocado correctamente desde el controller.

```
// Log antes:   EnrollmentController.create() — 35ms
// Log después: EnrollmentService.create()    — 34ms (mismo tiempo, misma respuesta)
```

---

### ✅ Cambio 2 — ADR-002: ChurchService Singleton

**Archivos modificados:**

- ✨ `ChurchService.java` — nueva clase
- ✏️ `CourseController.java` — eliminado `requireChurch()`, inyecta `ChurchService`
- ✏️ `EnrollmentController.java` — ídem
- ✏️ `OfferingController.java` — ídem
- ✏️ `PersonController.java` — ídem
- ✏️ `DashboardController.java` — ídem

**Evidencia funcional:**  
Se verificó con el endpoint `GET /api/courses` que retorna la lista correcta. Se inspeccionó con Spring Actuator que existe una única instancia de `ChurchService` en el contexto.

---

### ✅ Cambio 3 — ADR-003: GlobalExceptionHandler

**Archivos modificados:**

- ✨ `GlobalExceptionHandler.java`
- ✨ `ErrorResponse.java`
- ✨ `ChurchNotFoundException.java`
- ✨ `PersonNotFoundException.java`

**Evidencia funcional:**  
Se realizó `GET /api/enrollments` sin iglesia registrada. Antes retornaba `400 Bad Request` con texto plano. Después retorna:

```json
{
  "timestamp": "2025-03-07T10:15:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Debe registrar una iglesia primero",
  "path": "/api/enrollments"
}
```

---

### ✅ Cambio 4 — ADR-005: DTOs separados

**Archivos modificados:**

- ✨ `dto/request/EnrollmentRequest.java`
- ✨ `dto/response/EnrollmentResponse.java`
- ✨ `dto/request/LoginRequest.java`
- ✨ `dto/response/LoginResponse.java`
- ✏️ `AuthController.java`, `EnrollmentController.java` — actualizados imports

**Evidencia funcional:**  
`POST /api/auth/login` y `POST /api/enrollments` responden con la misma estructura JSON. Los DTOs son ahora importables desde servicios sin depender del controller.

---

### ✅ Cambio 5 — ADR-007: CoursesService Angular

**Archivos modificados:**

- ✨ `src/app/services/courses.service.ts`
- ✏️ `courses.component.ts` — inyecta `CoursesService` en lugar de `ApiService`
- ✏️ `src/environments/environment.ts` — URL base definida

**Evidencia funcional:**  
La vista de cursos (`/courses`) carga correctamente los datos. La URL del API se toma ahora de `environment.apiUrl`. Se verificó con las DevTools del navegador que las peticiones HTTP se realizan correctamente al endpoint `/api/courses`.

---

## 8. Consecuencias Generales

### Impacto Positivo

| Área                       | Mejora                                                                |
| -------------------------- | --------------------------------------------------------------------- |
| **Mantenibilidad**         | Código organizado por capas y responsabilidades claras                |
| **Testabilidad**           | Servicios desacoplados del contexto HTTP → unit tests más simples     |
| **Escalabilidad**          | Agregar nuevos tipos de pago o módulos sin modificar código existente |
| **Seguridad**              | Manejo centralizado de sesiones expiradas (401) en el interceptor     |
| **Experiencia de usuario** | Mensajes de error consistentes y notificaciones automáticas           |
| **Onboarding**             | Estructura de paquetes autoexplicativa para nuevos desarrolladores    |

### Trade-offs Aceptados

| Trade-off                                          | Justificación                                                 |
| -------------------------------------------------- | ------------------------------------------------------------- |
| Mayor número de clases y archivos                  | El incremento en organización supera el costo de más archivos |
| Refactor de imports en archivos existentes         | Automatizable con herramientas del IDE (IntelliJ, VS Code)    |
| Curva de aprendizaje en Signals y Strategy Pattern | Patrones ampliamente documentados con ejemplos claros         |

### Deuda Técnica Pendiente

- [ ] Implementar pruebas unitarias en el backend (actualmente en cero)
- [ ] Agregar `@ManyToOne Payment` formal en `Enrollment` y `Offering` para reemplazar `Long paymentId`
- [ ] Configurar variables de entorno para credenciales de la BD en `application.properties`
- [ ] Implementar paginación en endpoints que retornan listas (`/api/people`, `/api/payments`)

---

_Documento generado como parte del Parcial Corte 1 — Arquitectura de Software_  
_Karina Cantillo Plaza · Sebastian Osorio Fierro · Marzo 2025_
