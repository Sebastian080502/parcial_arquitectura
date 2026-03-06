# ADR-002: Usar DTOs y Adapter/Mapper para separar API y entidades

## Estado

Propuesto

## Contexto

Las entidades JPA del sistema están muy cerca de la capa HTTP, lo que puede exponer detalles internos del modelo de persistencia y acoplar la API REST al esquema de base de datos.

## Decisión

Se crearán DTOs de entrada y salida por módulo, por ejemplo:

- `LoginRequest`
- `LoginResponse`
- `ChurchRequest`
- `ChurchResponse`
- `PersonRequest`
- `PersonResponse`

Se implementará un patrón **Adapter** mediante mappers manuales o clases dedicadas:

- `ChurchMapper`
- `PersonMapper`
- `PaymentMapper`

## Justificación

Se busca desacoplar:

- la representación HTTP,
- la lógica de negocio,
- la persistencia.

Esto aplica **SRP** y reduce el impacto de cambios en entidades JPA sobre el contrato público del API.

## Consecuencias positivas

- La API se vuelve más estable.
- Se controlan mejor los campos expuestos.
- Se evita fuga de estructura interna.

## Trade-offs

- Más clases auxiliares.
- Conversión adicional entre DTO y entidad.

## Archivos objetivo

- Nuevos DTOs en `dto/`
- Nuevos mappers en `mapper/`
- Refactor de controladores
