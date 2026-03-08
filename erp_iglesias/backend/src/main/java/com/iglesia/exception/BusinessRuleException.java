package com.iglesia.exception;

/**
 * Excepción genérica para violaciones de reglas de negocio.
 * Ejemplo: Persona no pertenece a la iglesia actual.
 */
public class BusinessRuleException extends RuntimeException {
    
    public BusinessRuleException(String message) {
        super(message);
    }
}

