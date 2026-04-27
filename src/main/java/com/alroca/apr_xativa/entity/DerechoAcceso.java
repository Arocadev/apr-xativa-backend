package com.alroca.apr_xativa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "derechos_acceso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerechoAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = true)
    private Vehiculo vehiculo;

    @Column(name = "matricula_invitado", length = 10)
    private String matriculaInvitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_derecho", nullable = false, length = 10)
    private TipoDerecho tipoDerecho;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acred", nullable = false, length = 10)
    private Vehiculo.TipoAcred tipoAcred;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum TipoDerecho {
        PERMANENTE, PUNTUAL
    }
}