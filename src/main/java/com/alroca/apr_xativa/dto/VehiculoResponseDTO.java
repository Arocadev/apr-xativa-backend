package com.alroca.apr_xativa.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehiculoResponseDTO {
    private Long id;
    private String matricula;
    private String tipoAcred;
    private boolean activo;
    private LocalDateTime createdAt;
    private Long usuarioId;
}