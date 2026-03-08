package com.iglesia.exception;

public class OfferingNotFoundException extends RuntimeException {
    public OfferingNotFoundException(Long id) {
        super("Ofrenda no encontrada con ID: " + id);
    }
}