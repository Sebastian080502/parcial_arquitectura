package com.iglesia.exception;

import com.iglesia.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Intercepta TODAS las excepciones lanzadas en los controladores
 * y las convierte en respuestas HTTP estandarizadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Logger para registrar los errores
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Maneja: Cuando no existe iglesia registrada
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(ChurchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChurchNotFound(
            ChurchNotFoundException ex, 
            HttpServletRequest request) {
        
        // 1. Registrar en log
        log.warn("Church not found: {}", ex.getMessage());
        
        // 2. Construir respuesta estandarizada
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        // 3. Retornar con código 400
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Maneja: Cuando no se encuentra una persona
     * HTTP 404 Not Found
     */
    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePersonNotFound(
            PersonNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Person not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Maneja: Cuando no se encuentra un curso
     * HTTP 404 Not Found
     */
    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCourseNotFound(
            CourseNotFoundException ex,
            HttpServletRequest request) {
        
        log.warn("Course not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Maneja: Violaciones de reglas de negocio
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex,
            HttpServletRequest request) {
        
        log.warn("Business rule violation: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * MANEJO GENÉRICO - CUALQUIER OTRO ERROR INESPERADO
     * HTTP 500 Internal Server Error
     * 
     * Este método es la red de seguridad. Si ocurre una excepción
     * que NO tenemos específicamente manejada, cae aquí.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {
        
        // Registrar el error COMPLETO para debugging
        log.error("Unexpected error occurred", ex);
        
        // Mensaje genérico para el cliente (no exponemos detalles internos)
        String message = "Ocurrió un error inesperado en el servidor";
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}