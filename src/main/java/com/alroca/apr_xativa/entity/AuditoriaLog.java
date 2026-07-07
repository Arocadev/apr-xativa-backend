package com.alroca.apr_xativa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Evento evento;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_dni", length = 10)
    private String usuarioDni;

    @Column(length = 500)
    private String detalle;

    @Column(name = "realizado_por_id")
    private Long realizadoPorId;

    @Column(name = "realizado_por_dni", length = 10)
    private String realizadoPorDni;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Evento {
        REGISTRO_USUARIO,
        SOLICITUD_APROBADA,
        SOLICITUD_RECHAZADA,
        VEHICULO_ALTA,
        VEHICULO_BAJA,
        DERECHO_PERMANENTE_CREADO,
        DERECHO_PUNTUAL_CREADO,
        DERECHO_INVITADO_CREADO,
        DERECHO_ELIMINADO,
        LOGIN,
        LOGOUT
    }
}