package com.peluqueria.gestioncitas.exception;

/**
 * Excepción personalizada para cuando no se encuentra un recurso
 * Se usa principalmente en operaciones de autenticación
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}