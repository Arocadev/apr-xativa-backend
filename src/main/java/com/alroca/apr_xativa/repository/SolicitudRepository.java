package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByUsuarioId(Long usuarioId);
    List<Solicitud> findByEstado(Solicitud.Estado estado);
    Page<Solicitud> findByEstado(Solicitud.Estado estado, Pageable pageable);
    Optional<Solicitud> findByUsuarioIdAndEstado(Long usuarioId, Solicitud.Estado estado);
}