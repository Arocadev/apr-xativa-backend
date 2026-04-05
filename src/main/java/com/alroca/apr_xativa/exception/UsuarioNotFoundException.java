package com.alroca.apr_xativa.exception;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String identificador) {
        super("Usuario no encontrado: " + identificador);
    }
}
