package com.taller.mecanica.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    public static ResourceNotFoundException de(String recurso, Long id) {
        return new ResourceNotFoundException(recurso + " no encontrado con id " + id);
    }
}
