package com.alroca.apr_xativa.config;

import com.alroca.apr_xativa.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNotFound(UsuarioNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(VehiculoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVehiculoNotFound(VehiculoNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SolicitudNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSolicitudNotFound(SolicitudNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DerechoAccesoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDerechoNotFound(DerechoAccesoNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicado(DuplicadoException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(AccesoNoPermitidoException.class)
    public ResponseEntity<Map<String, Object>> handleAccesoNoPermitido(AccesoNoPermitidoException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(FechaNoValidaException.class)
    public ResponseEntity<Map<String, Object>> handleFechaNoValida(FechaNoValidaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "DNI o contrasena incorrectos");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta accion");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Error de validacion");
        return buildResponse(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(body);
    }
}