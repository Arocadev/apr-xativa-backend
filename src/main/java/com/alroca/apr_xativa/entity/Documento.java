package com.alroca.apr_xativa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_doc", nullable = false, length = 80)
    @NotBlank
    private String tipoDoc;

    @Column(nullable = false, length = 500)
    @NotBlank
    private String ruta;

    @Column(name = "subido_at", nullable = false, updatable = false)
    private LocalDateTime subidoAt;

    @PrePersist
    protected void onCreate() {
        subidoAt = LocalDateTime.now();
    }
}
