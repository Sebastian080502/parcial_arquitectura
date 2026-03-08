package com.iglesia.exception;

public class ChurchNotFoundException extends RuntimeException {
    public ChurchNotFoundException() {
        super("No hay iglesia registrada");
    }

    public ChurchNotFoundException(String message) {
        super(message);
    }
}