package com.taller.mecanica.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
