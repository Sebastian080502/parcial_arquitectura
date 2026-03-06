# ADR-004: Introducir un Facade para autenticación

## Estado

Propuesto

## Contexto

El proceso de autenticación involucra múltiples piezas:

- `AuthController`
- `JwtService`
- `PasswordEncoder`
- `AppUserRepository`
- filtros y configuración de seguridad

Esto hace que la lógica de login y emisión de token quede dispersa.

## Decisión

Se implementará un patrón **Facade** llamado `AuthFacade` o `AuthService` que encapsule:

- búsqueda de usuario,
- validación de contraseña,
- generación de JWT,
- construcción de la respuesta de login.

El controlador de autenticación solo delegará al facade.

## Justificación

El patrón **Facade** reduce complejidad y mejora la cohesión del módulo de seguridad.

También favorece **SRP**, porque el controlador ya no debe coordinar múltiples dependencias directamente.

## Consecuencias positivas

- Flujo de autenticación más claro.
- Menor acoplamiento entre controller y componentes de seguridad.
- Más fácil de probar.

## Trade-offs

- Se agrega una nueva clase de orquestación.
- Requiere mover lógica fuera del controlador actual.

## Archivos objetivo

- `AuthController.java`
- `JwtService.java`
- Nuevo `AuthFacade.java`
