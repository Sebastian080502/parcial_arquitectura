package com.iglesia.dto.response;

import java.time.LocalDateTime;


public record ErrorResponse(
    LocalDateTime timestamp,  // Cuándo ocurrió el error
    int status,               // Código HTTP (400, 404, 500...)
    String error,             // Nombre del error ("Bad Request", "Not Found")
    String message,           // Mensaje descriptivo para el cliente
    String path               // URL que causó el error
) {}
