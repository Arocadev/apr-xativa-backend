package com.alroca.apr_xativa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VehiculoRequestDTO {

    @NotBlank
    private String matricula;

    @NotBlank
    private String tipoAcred;
}