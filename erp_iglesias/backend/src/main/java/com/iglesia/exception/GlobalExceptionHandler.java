package com.iglesia.exception;

import com.iglesia.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * Intercepta TODAS las excepciones lanzadas en los controladores
 * y las convierte en respuestas HTTP estandarizadas.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Cuando no existe iglesia registrada
     * HTTP 400
     */
    @ExceptionHandler(ChurchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChurchNotFound(
            ChurchNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Church not found: {}", ex.getMessage());

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
     * Cuando no se encuentra una persona
     * HTTP 404
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
     * Cuando no se encuentra un curso
     * HTTP 404
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
     * Violación de reglas de negocio
     * HTTP 400
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
     * Cuando el usuario no tiene permisos
     * HTTP 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "No tienes permisos para acceder a este recurso",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Cualquier error inesperado
     * HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error inesperado en el servidor",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}