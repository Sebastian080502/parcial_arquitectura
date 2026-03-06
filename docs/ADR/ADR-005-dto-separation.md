# ADR-005: Centralizar manejo de errores con ControllerAdvice

## Estado

Propuesto

## Contexto

El sistema no posee una estrategia central de manejo de excepciones visible a nivel de API. Esto puede producir respuestas inconsistentes y dificultar el diagnóstico de errores funcionales o de validación.

## Decisión

Se implementará un manejador global de excepciones usando:

- `@RestControllerAdvice`
- `@ExceptionHandler`

Se normalizarán respuestas para:

- validaciones,
- recursos no encontrados,
- errores de autenticación,
- errores internos.

## Justificación

Mejora la consistencia del API y reduce duplicación en controladores.

Aplica **SRP**, porque el tratamiento de errores deja de repetirse en múltiples clases.

## Consecuencias positivas

- Respuestas homogéneas.
- Mejor experiencia para frontend.
- Mayor trazabilidad de fallos.

## Trade-offs

- Requiere definir un contrato común de error.
- Puede ocultar errores si no se registra adecuadamente.

## Archivos objetivo

- Nuevo `GlobalExceptionHandler.java`
- Refactor menor en controladores
