package com.alroca.apr_xativa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolicitudResponseDTO {
    private Long id;
    private Long usuarioId;
    private String usuarioDni;
    private String usuarioNombre;
    private String estado;
    private String observaciones;
    private Long adminId;
    private LocalDateTime createdAt;
    private LocalDateTime gestionadaAt;
}