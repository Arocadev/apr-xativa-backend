package com.alroca.apr_xativa.exception;

public class VehiculoNotFoundException extends RuntimeException {
    public VehiculoNotFoundException(Long id) {
        super("Vehiculo no encontrado con id: " + id);
    }
}
