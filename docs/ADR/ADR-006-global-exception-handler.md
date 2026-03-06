# ADR-006: Fortalecer configuración externa y secretos

## Estado

Propuesto

## Contexto

La configuración actual usa valores por defecto para conexión a base de datos y para el secreto JWT. En `application.properties` existe un valor por defecto para `app.jwt.secret`, y el `docker-compose.yml` define credenciales simples para base de datos y secreto JWT.

## Decisión

Toda configuración sensible deberá venir desde variables de entorno obligatorias:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`

Se eliminarán secretos por defecto del código fuente.

## Justificación

Esta decisión mejora la seguridad y alinea el proyecto con buenas prácticas de despliegue.

Evita:

- exposición de secretos,
- entornos inseguros por configuración por defecto,
- ambigüedad entre local y docker.

## Consecuencias positivas

- Menor riesgo de fuga de secretos.
- Mejor portabilidad entre ambientes.
- Mayor control operacional.

## Trade-offs

- El entorno local requiere configuración explícita.
- Más dependencia de `.env` o variables del sistema.

## Archivos objetivo

- `application.properties`
- `docker-compose.yml`
- README de despliegue
