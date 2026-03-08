package com.iglesia.exception;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(Long id) {
        super("Inscripción no encontrada con ID: " + id);
    }
}