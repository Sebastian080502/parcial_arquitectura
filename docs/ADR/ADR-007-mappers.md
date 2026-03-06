# ADR-007: Reorganizar paquetes por responsabilidad y dominio

## Estado

Propuesto

## Contexto

Actualmente las clases del backend se encuentran agrupadas en un paquete plano `com.iglesia`, lo cual dificulta ubicar responsabilidades y escala mal conforme crece el sistema.

## Decisión

Se reorganizará la estructura del backend por capas y/o dominios:

Ejemplo:

- `com.iglesia.controller`
- `com.iglesia.service`
- `com.iglesia.repository`
- `com.iglesia.security`
- `com.iglesia.model`
- `com.iglesia.dto`
- `com.iglesia.mapper`
- `com.iglesia.exception`

## Justificación

Mejora la mantenibilidad, reduce desorden estructural y favorece la lectura arquitectónica del proyecto.

Aplica **SRP** a nivel organizacional y sienta base para una futura arquitectura más limpia.

## Consecuencias positivas

- Mejor navegación del código.
- Más claridad para nuevos desarrolladores.
- Menor mezcla de responsabilidades.

## Trade-offs

- Refactor mecánico amplio.
- Riesgo de errores por imports si no se hace cuidadosamente.

## Archivos objetivo

- Todo `backend/src/main/java/com/iglesia`
