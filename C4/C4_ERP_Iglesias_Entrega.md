# Documentación Arquitectónica C4
## ERP Iglesias — Sistema de Gestión Administrativa

---

<div align="center">

**Actividad:** Diagramas de Arquitectura C4  
**Estudiante:** Karina Cantillo Plaza  
**Profesor:** Luis Ángel Vargas  
**Materia:** Arquitectura de Software — Sexto Semestre  
**Fecha:** Marzo 2025  
**Repositorio:** `lanvargas94/erp_iglesias`

</div>

---

## Tabla de Contenidos

- [Introducción](#introducción)
- [Nivel 1 — Diagrama de Contexto](#nivel-1--diagrama-de-contexto)
- [Nivel 2 — Diagrama de Contenedores](#nivel-2--diagrama-de-contenedores)
- [Nivel 3 — Diagrama de Componentes](#nivel-3--diagrama-de-componentes)
- [Conclusión](#conclusión)

---

## Introducción

**ERP Iglesias** es una aplicación web de gestión administrativa diseñada para iglesias. Permite administrar miembros (personas), cursos, inscripciones, ofrendas y pagos, con un esquema de control de acceso basado en roles (`ADMIN` y `CLIENT`).

El sistema sigue una arquitectura cliente-servidor moderna:

| Capa | Tecnología | Versión |
|------|-----------|---------|
| Frontend | Angular + TypeScript + Nginx | Angular 17.3 |
| Backend | Java + Spring Boot | Java 17 / SB 3.2.3 |
| Seguridad | Spring Security + JWT | jjwt 0.11.5 |
| Base de datos | PostgreSQL | Latest |
| Infraestructura | Docker + Docker Compose | Latest |

Este documento presenta la arquitectura del sistema mediante el **modelo C4**, en tres niveles de abstracción: Contexto, Contenedores y Componentes. Cada nivel responde a una pregunta arquitectónica específica y se construye sobre el anterior, manteniendo coherencia a lo largo de toda la documentación.

---

## Nivel 1 — Diagrama de Contexto

### ¿Qué muestra este diagrama?

El Diagrama de Contexto representa el sistema ERP Iglesias como una **caja negra**, mostrando únicamente su alcance y las interacciones con los actores externos. No revela ningún detalle tecnológico interno. Es el nivel más alto de abstracción del modelo C4 y responde a la pregunta: *¿qué es el sistema y con quién interactúa?*

### Diagrama

>`C1_Contexto.png`

![Diagrama de Contexto — ERP Iglesias](./img/C1_Contexto.png)

---

### Explicación

#### Actores identificados

Se identificaron dos actores directos del sistema, basados en los roles definidos en el código fuente (`UserRole.java`):

**Administrador del sistema** corresponde al rol `ADMIN` en el backend. Es el actor estratégico: configura la iglesia (con restricción de una única entidad por sistema), gestiona los usuarios del sistema, administra el catálogo de cursos y confirma o rechaza pagos. Su rol está orientado a la supervisión y control global.

**Personal Administrativo** corresponde al rol `CLIENT` en el backend. Es el actor operativo: registra miembros (personas), gestiona inscripciones a cursos, registra ofrendas y consulta estados de pagos y reportes. Su interacción es constante y centrada en la gestión diaria.

#### ¿Por qué elegimos estos elementos?

Se eligieron exactamente los actores que existen en el código fuente del proyecto, específicamente en el enum `UserRole` que define `ADMIN` y `CLIENT`. No se inventaron roles adicionales para mantener fidelidad al sistema real. No se identificaron sistemas externos integrados, ya que el repositorio no contiene integraciones con pasarelas de pago, servicios de correo ni APIs externas — el sistema es una solución autónoma.

#### ¿Cómo se relacionan entre sí?

Las relaciones son **bidireccionales**: los actores envían solicitudes al sistema mediante `HTTPS` y reciben respuestas en formato `JSON`:

- El **Administrador** configura el sistema, gestiona usuarios y confirma pagos vía navegador web.
- El **Personal Administrativo** registra datos operativos y consulta información vía navegador web.
- El sistema retorna confirmaciones, listados y reportes a ambos actores.

#### ¿Qué nivel C4 representa?

Este diagrama representa el **Nivel 1 (Contexto)** del modelo C4. Su propósito es establecer el límite del sistema y su entorno, sin revelar decisiones tecnológicas internas.

#### Decisiones y supuestos tomados

- Se decidió **no incluir un actor de acceso público** porque el sistema requiere autenticación para todas sus rutas (`anyRequest().authenticated()` en `SecurityConfig.java`). No existe acceso anónimo.
- Se decidió **no incluir sistemas externos** porque no hay evidencia en el repositorio de integraciones con servicios de terceros.
- Se asumió que ambos actores acceden desde un **navegador web estándar**, consistente con la arquitectura SPA de Angular.

---

## Nivel 2 — Diagrama de Contenedores

### ¿Qué muestra este diagrama?

El Diagrama de Contenedores amplía el sistema ERP Iglesias y muestra las **piezas tecnológicas** que lo componen: las aplicaciones y almacenes de datos que se ejecutan para que el sistema funcione. Responde a la pregunta: *¿cómo está dividido el sistema a nivel tecnológico?*

### Diagrama

>  `C2_Contenedores.png`

![Diagrama de Contenedores — ERP Iglesias](./img/C2_Contenedores.png)

---

### Explicación

#### Contenedores identificados

Se identificaron tres contenedores, todos evidenciados directamente en el repositorio:

**Frontend Web** (`Angular 17 + TypeScript + Nginx`) es la aplicación de una sola página que presenta la interfaz de usuario. En desarrollo se ejecuta en el puerto `:4200` mediante `ng serve`. En producción, los archivos compilados son servidos por Nginx en el puerto `:80`, como se evidencia en el archivo `nginx.conf` del proyecto.

**API REST** (`Spring Boot 3.2.3 + Java 17 + JWT`) contiene toda la lógica de negocio del sistema. Expone endpoints REST en el puerto `:8080`, valida tokens JWT mediante `JwtAuthFilter`, aplica reglas de negocio en los servicios y accede a los datos mediante JPA. Es el contenedor central del sistema.

**Base de datos** (`PostgreSQL`) almacena de forma persistente toda la información del dominio: personas, cursos, inscripciones, ofrendas, pagos y usuarios. Se comunica con el backend mediante JDBC/JPA (Hibernate), configurado en `application.properties`.

#### ¿Por qué elegimos estos elementos?

Los tres contenedores se eligieron porque están **directamente evidenciados** en el repositorio:
- El frontend existe en `frontend/` con su `package.json` (Angular 17.3) y `nginx.conf`
- El backend existe en `backend/` con su `pom.xml` (Spring Boot 3.2.3)
- La base de datos está definida en `docker-compose.yml` como servicio `postgres`

No se agregaron contenedores adicionales (caché, colas de mensajes) porque no hay evidencia de ellos en el repositorio.

#### ¿Cómo se relacionan entre sí?

El flujo de una petición típica es:

1. Los actores acceden al **Frontend Web** mediante `HTTPS` desde el navegador
2. El **Frontend Web** consume los endpoints del **API REST** enviando peticiones `HTTP/JSON` con el token JWT en el encabezado `Authorization`
3. El **API REST** retorna respuestas `JSON` al frontend
4. El **API REST** lee y escribe datos en la **Base de datos** mediante `JPA/SQL`

Todos los contenedores son orquestados con **Docker Compose**, definido en `docker-compose.yml`.

#### ¿Qué nivel C4 representa?

Este diagrama representa el **Nivel 2 (Contenedores)** del modelo C4. Muestra las decisiones tecnológicas principales y cómo los contenedores se comunican entre sí.

#### Decisiones y supuestos tomados

- Se decidió **integrar Nginx dentro del contenedor Frontend** (no como contenedor separado) porque en el `Dockerfile` del frontend, Nginx y Angular están en el mismo contenedor en un proceso de build multi-stage.
- Se incluyó **Docker Compose como nota de orquestación** en la descripción, pero no como contenedor, ya que es una herramienta de despliegue, no una aplicación en ejecución.
- Se especificaron los **puertos reales** del proyecto (`:8080`, `:4200`, `:80`) porque están definidos en `docker-compose.yml` y `application.properties`.

---

## Nivel 3 — Diagrama de Componentes

### ¿Qué muestra este diagrama?

El Diagrama de Componentes profundiza en el interior del contenedor **API REST (Spring Boot)** — el más relevante arquitectónicamente — mostrando los componentes internos que lo conforman, sus responsabilidades y las relaciones entre ellos. Responde a la pregunta: *¿qué hay dentro del backend?*

### Diagrama

> `C3_Componentes.png`

![Diagrama de Componentes — ERP Iglesias](./img/C3_Componentes.png)

---

### Explicación

#### Componentes identificados

Se identificaron siete componentes arquitectónicos, todos evidenciados en el código fuente y mejorados mediante los ADRs implementados:

**Seguridad JWT** (`JwtAuthFilter`, `JwtService`, `SecurityConfig`) es el componente transversal que intercepta **todas** las peticiones HTTP antes de que lleguen a los controladores. Valida el token JWT, extrae el rol del usuario y establece la autenticación en el contexto de Spring Security.

**Controladores REST** (`AuthController`, `ChurchController`, `CourseController`, `EnrollmentController`, `OfferingController`, `PaymentController`, `PersonController`) reciben las peticiones HTTP, validan los datos de entrada con `@Valid` y delegan la ejecución en los servicios. No contienen lógica de negocio — implementado mediante **ADR-001** (Service Layer + SRP).

**Manejador global de excepciones** (`GlobalExceptionHandler`, `ErrorResponse`) captura todas las excepciones de dominio y las transforma en respuestas JSON estandarizadas. Implementado mediante **ADR-003** (@ControllerAdvice + OCP).

**Servicios de negocio** (`ChurchService`, `EnrollmentService`, `OfferingService`, `PaymentService`, `UserService`, `AuthService`) encapsulan la lógica de negocio con `@Service` y `@Transactional`. `ChurchService` centraliza la obtención de la iglesia — **ADR-002** (Singleton + DRY). Los demás servicios fueron extraídos de los controladores mediante **ADR-001**.

**DTOs** (`dto/request/`, `dto/response/`) definen la estructura de los datos intercambiados con el frontend. Movidos a paquetes independientes mediante **ADR-005** (DTO Pattern + SRP).

**Repositorios JPA** (`ChurchRepository`, `CourseRepository`, `EnrollmentRepository`, `OfferingRepository`, `PaymentRepository`, `PersonRepository`, `AppUserRepository`) son la capa de abstracción sobre la base de datos mediante Spring Data JPA.

**Entidades JPA** (`Church`, `Course`, `Enrollment`, `Offering`, `Payment`, `Person`, `AppUser`) modelan las tablas de la base de datos y sus relaciones mediante anotaciones Hibernate.

#### ¿Por qué elegimos estos elementos?

Se eligieron estos componentes porque representan **unidades con responsabilidad clara** dentro del backend, no archivos aislados. Se eligió el **backend como contenedor a profundizar** porque es donde se concentra la mayor complejidad arquitectónica y donde se aplicaron los 5 cambios de los ADRs implementados.

#### ¿Cómo se relacionan entre sí?

El flujo de una petición típica dentro del backend es:

1. El **Frontend Web** envía una petición con token JWT
2. **Seguridad JWT** intercepta, valida el token y autoriza el acceso
3. Los **Controladores REST** reciben la petición, validan con **DTOs** y delegan en **Servicios**
4. Si hay error, el **Manejador global** lo captura y retorna `ErrorResponse`
5. Los **Servicios** aplican lógica, convierten entidades en **DTOs** y usan **Repositorios**
6. Los **Repositorios** operan sobre **Entidades JPA** y ejecutan SQL en **PostgreSQL**

Las dependencias fluyen en **una sola dirección**: `Controllers → Services → Repositories`, sin dependencias inversas.

#### ¿Qué nivel C4 representa?

Este diagrama representa el **Nivel 3 (Componentes)** del modelo C4. Muestra la estructura interna de un contenedor específico con el nivel de detalle suficiente para entender cómo está organizado el código.

#### Decisiones y supuestos tomados

- Se decidió **agrupar los controladores en un solo componente** para mantener el diagrama legible. Lo mismo aplica para servicios y repositorios.
- Se incluyeron los **DTOs como componente** porque representan una decisión arquitectónica explícita documentada en **ADR-005**.
- Se incluyó la **Seguridad JWT como componente transversal** porque intercepta el flujo antes de los controladores.
- Se asumió que las **Entidades JPA** merecen representación propia porque modelan el dominio y son el puente entre la lógica de negocio y la base de datos.

---

## Conclusión

La arquitectura de ERP Iglesias sigue un patrón de **tres capas** (presentación, lógica, datos) con separación clara de responsabilidades en cada nivel del modelo C4.

Los tres niveles mantienen coherencia entre sí:

| Nivel C4 | Elemento | Se descompone en |
|----------|---------|-----------------|
| Contexto (N1) | ERP Iglesias | ↓ |
| Contenedores (N2) | Frontend + API REST + PostgreSQL | ↓ |
| Componentes (N3) | Controllers + Services + Repositories + Security + DTOs + Entities + ExceptionHandler | — |

Las decisiones arquitectónicas documentadas en los ADRs implementados — **Service Layer (ADR-001)**, **ChurchService Singleton (ADR-002)**, **GlobalExceptionHandler (ADR-003)**, **DTOs separados (ADR-005)** y **Angular Services (ADR-007)** — mejoran directamente la mantenibilidad, escalabilidad y testeabilidad del sistema, aplicando principios **SOLID** y patrones de diseño reconocidos en la industria.

El sistema resultante tiene componentes con responsabilidades claras, dependencias unidireccionales y un flujo de datos predecible, lo que facilita tanto su comprensión como su evolución futura.

---

*Actividad C4 — Arquitectura de Software · Marzo 2025*  
*Karina Cantillo Plaza · Profesor: Luis Ángel Vargas*