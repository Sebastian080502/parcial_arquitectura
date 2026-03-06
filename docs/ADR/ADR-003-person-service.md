# ADR-003: Implementar Factory Method para creación de pagos

## Estado

Propuesto

## Contexto

El módulo de pagos maneja tipos como `PaymentType` y estados de pago. La creación de objetos `Payment` puede crecer con reglas específicas por tipo de pago, validaciones y configuraciones particulares.

## Decisión

Se implementará un patrón **Factory Method** para centralizar la creación de pagos.

Ejemplo:

- `PaymentFactory`
- `createPayment(PaymentType type, ...)`

La fábrica se encargará de:

- instanciar el objeto,
- asignar estado inicial,
- validar campos obligatorios,
- encapsular reglas de creación.

## Justificación

Aplica **OCP (Open/Closed Principle)**:

- el sistema podrá agregar nuevos tipos de pago sin modificar múltiples controladores o servicios.

También reduce lógica condicional repetida.

## Consecuencias positivas

- Creación consistente de pagos.
- Menos `if/else` o `switch` dispersos.
- Mejor escalabilidad del módulo financiero.

## Trade-offs

- Añade una capa de abstracción.
- Requiere reorganizar el flujo actual de creación.

## Archivos objetivo

- `Payment.java`
- `PaymentController.java`
- Nuevo `PaymentFactory.java`
