package com.alroca.apr_xativa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    @NotBlank
    private String dni;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String nombre;

    @Column(nullable = false, length = 150)
    @NotBlank
    private String apellidos;

    @Column(nullable = false, unique = true, length = 150)
    @Email
    @NotBlank
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Rol rol;

    @Column(nullable = false, length = 5)
    @NotBlank
    private String tipo;

    @Column(name = "num_camas", nullable = false)
    private int numCamas = 0;

    @Column(name = "num_plazas", nullable = false)
    private int numPlazas = 0;

    @Column(name = "num_trabajadores", nullable = false)
    private int numTrabajadores = 0;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Rol {
        USER, ADMIN
    }
}