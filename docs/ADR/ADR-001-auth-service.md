# ADR-001: Introducir capa de servicios para desacoplar controladores

## Estado

Propuesto

## Contexto

El sistema actual concentra gran parte de la lógica de aplicación alrededor de controladores, repositorios y clases del mismo paquete `com.iglesia`, lo que incrementa el acoplamiento y dificulta las pruebas unitarias, la reutilización de lógica y la evolución del sistema.

## Decisión

Se implementará una capa de servicios por módulo de negocio:

- `ChurchService`
- `PersonService`
- `CourseService`
- `EnrollmentService`
- `OfferingService`
- `PaymentService`
- `AuthService`

Los controladores solo deberán:

1. recibir la petición,
2. delegar al servicio,
3. devolver la respuesta HTTP.

## Justificación

Esta decisión aplica el principio **SRP (Single Responsibility Principle)**, separando la responsabilidad de exposición HTTP de la lógica de negocio.

Además, mejora:

- mantenibilidad,
- testabilidad,
- reutilización,
- legibilidad arquitectónica.

## Consecuencias positivas

- Controladores más delgados.
- Lógica centralizada.
- Menor duplicación.
- Mejor preparación para futuras refactorizaciones.

## Trade-offs

- Aumenta el número de clases.
- Requiere refactor inicial de los controladores existentes.

## Archivos objetivo

- `*Controller.java`
- Nuevos archivos `*Service.java`
