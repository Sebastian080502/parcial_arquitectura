# ADR-008: Definir política CORS explícita y centralizada

## Estado

Propuesto

## Contexto

El frontend y el backend se despliegan en puertos distintos (`4200` y `8080`/`8085` según entorno), por lo que el sistema depende de peticiones cross-origin. Una política CORS implícita o incompleta afecta el login y la comunicación entre capas.

## Decisión

Se definirá una configuración CORS explícita en la capa de seguridad para permitir únicamente los orígenes necesarios en desarrollo y despliegue.

Ejemplo de origen permitido:

- `http://localhost:4200`

## Justificación

Esto evita fallos de integración frontend-backend y mejora el control de seguridad.

Además, documenta formalmente el contrato de comunicación entre ambos componentes.

## Consecuencias positivas

- Menos errores de preflight.
- Mayor previsibilidad en entorno local.
- Mejor seguridad al restringir orígenes.

## Trade-offs

- Requiere distinguir entre desarrollo y producción.
- Puede fallar si no se documentan correctamente los orígenes permitidos.

## Archivos objetivo

- `SecurityConfig.java`
- Configuración de entorno frontend
