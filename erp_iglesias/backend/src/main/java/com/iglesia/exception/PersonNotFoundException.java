package com.iglesia.exception;

/**
 * Se lanza cuando no se encuentra una persona por su ID.
 */
public class PersonNotFoundException extends RuntimeException {
    
    public PersonNotFoundException(Long id) {
        super("Persona no encontrada con ID: " + id);
    }
    
    public PersonNotFoundException(String message) {
        super(message);
    }
}
