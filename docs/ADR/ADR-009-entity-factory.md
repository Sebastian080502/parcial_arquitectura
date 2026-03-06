# ADR-009: Modelar relaciones explícitas entre pagos y entidades de negocio

## Estado

Propuesto

## Contexto

El dominio incluye tablas como `payments`, `enrollments`, `offerings`, `people`, `courses` y `churches`. Actualmente el vínculo entre pagos y otras entidades puede quedar representado de forma débil o implícita, lo que complica la integridad referencial y la trazabilidad del negocio.

## Decisión

Se revisará y refactorizará el modelo para que las relaciones entre:

- pagos,
- ofrendas,
- inscripciones,
- personas

queden expresadas mediante asociaciones claras en el modelo JPA y, de ser necesario, con claves foráneas consistentes en la base de datos.

## Justificación

Mejora la integridad del modelo, la trazabilidad financiera y la consistencia entre dominio y persistencia.

## Consecuencias positivas

- Modelo de datos más sólido.
- Consultas más claras.
- Menor ambigüedad funcional.

## Trade-offs

- Puede implicar migración de datos.
- Requiere revisar formularios y endpoints afectados.

## Archivos objetivo

- `Payment.java`
- `Offering.java`
- `Enrollment.java`
- Repositorios y controladores asociados
