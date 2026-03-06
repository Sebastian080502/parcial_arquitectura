# ADR-010: Documentar formalmente el API con OpenAPI/Swagger

## Estado

Propuesto

## Contexto

El sistema expone múltiples endpoints REST para autenticación y gestión de módulos como iglesias, personas, cursos, inscripciones, pagos y ofrendas. Sin una documentación formal, el consumo del API depende del código fuente o de pruebas manuales.

## Decisión

Se incorporará documentación OpenAPI/Swagger al backend para describir:

- endpoints,
- request bodies,
- responses,
- códigos de estado,
- autenticación JWT.

## Justificación

Esto mejora la mantenibilidad, acelera pruebas manuales y facilita integración entre frontend y backend.

También eleva la calidad de documentación del proyecto, lo cual aporta valor directo al parcial.

## Consecuencias positivas

- API explorable.
- Menor dependencia del código para entender contratos.
- Más facilidad para pruebas y sustentación.

## Trade-offs

- Requiere mantener la documentación actualizada.
- Aumenta ligeramente la configuración del proyecto.

## Archivos objetivo

- `pom.xml`
- Configuración Swagger/OpenAPI
- Controladores existentes
