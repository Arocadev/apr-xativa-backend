package com.alroca.apr_xativa.exception;

public class DerechoAccesoNotFoundException extends RuntimeException {
    public DerechoAccesoNotFoundException(Long id) {
        super("Derecho de acceso no encontrado con id: " + id);
    }
}