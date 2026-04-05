package com.alroca.apr_xativa.exception;

public class SolicitudNotFoundException extends RuntimeException {
    public SolicitudNotFoundException(Long id) {
        super("Solicitud no encontrada con id: " + id);
    }
}