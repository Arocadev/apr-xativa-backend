package com.alroca.apr_xativa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String rol;
    private String tipo;
    private int numCamas;
    private int numPlazas;
    private int numTrabajadores;
    private boolean activo;
    private LocalDateTime createdAt;
}