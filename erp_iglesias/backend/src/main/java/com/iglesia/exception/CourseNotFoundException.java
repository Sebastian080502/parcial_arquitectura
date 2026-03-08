 package com.iglesia.exception;

public class CourseNotFoundException extends RuntimeException {
    
    public CourseNotFoundException(Long id) {
        super("Curso no encontrado con ID: " + id);
    }
}