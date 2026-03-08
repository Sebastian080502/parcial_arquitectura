package com.iglesia.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super("Pago no encontrado con ID: " + id);
    }
}