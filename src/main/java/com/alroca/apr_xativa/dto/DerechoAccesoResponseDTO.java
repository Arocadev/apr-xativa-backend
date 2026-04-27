package com.alroca.apr_xativa.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DerechoAccesoResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long vehiculoId;
    private String matricula;
    private String matriculaInvitado;
    private String tipoDerecho;
    private String tipoAcred;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean activo;
    private LocalDateTime createdAt;
}
