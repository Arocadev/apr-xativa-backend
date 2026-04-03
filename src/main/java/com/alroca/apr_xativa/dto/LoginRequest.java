package com.alroca.apr_xativa.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String dni;

    @NotBlank
    private String password;
}