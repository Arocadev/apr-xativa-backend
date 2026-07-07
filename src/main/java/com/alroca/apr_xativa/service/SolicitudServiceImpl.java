package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Solicitud;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.SolicitudNotFoundException;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;

    @Override
    public Solicitud crear(Long usuarioId) {
        log.info("Intentando crear solicitud para usuario id: {}", usuarioId);
        solicitudRepository.findByUsuarioIdAndEstado(usuarioId, Solicitud.Estado.PENDIENTE)
                .ifPresent(s -> {
                    log.warn("Usuario id: {} ya tiene una solicitud pendiente", usuarioId);
                    throw new DuplicadoException("Ya tienes una solicitud pendiente");
                });
        Usuario usuario = usuarioService.findById(usuarioId);
        Solicitud solicitud = Solicitud.builder()
                .usuario(usuario)
                .estado(Solicitud.Estado.PENDIENTE)
                .build();
        log.info("Solicitud creada correctamente para usuario id: {}", usuarioId);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> findByUsuario(Long usuarioId) {
        log.debug("Listando solicitudes del usuario id: {}", usuarioId);
        return solicitudRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Solicitud> findPendientes() {
        log.debug("Listando solicitudes pendientes");
        return solicitudRepository.findByEstado(Solicitud.Estado.PENDIENTE);
    }

    @Override
    public Page<Solicitud> findAllPaginado(Pageable pageable) {
        log.info("Listando solicitudes paginadas: pagina={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return solicitudRepository.findAll(pageable);
    }

    @Override
    public Page<Solicitud> findPendientesPaginado(Pageable pageable) {
        log.info("Listando solicitudes pendientes paginadas");
        return solicitudRepository.findByEstado(Solicitud.Estado.PENDIENTE, pageable);
    }

    @Override
    public Solicitud aprobar(Long solicitudId, Long adminId) {
        log.info("Admin id: {} aprobando solicitud id: {}", adminId, solicitudId);
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));
        Usuario admin = usuarioService.findById(adminId);
        solicitud.setEstado(Solicitud.Estado.APROBADA);
        solicitud.setAdmin(admin);
        solicitud.setGestionadaAt(LocalDateTime.now());
        usuarioService.reactivar(solicitud.getUsuario().getId());
        log.info("Solicitud id: {} aprobada correctamente por admin id: {}", solicitudId, adminId);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public Solicitud rechazar(Long solicitudId, Long adminId, String observaciones) {
        log.info("Admin id: {} rechazando solicitud id: {}", adminId, solicitudId);
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));
        Usuario admin = usuarioService.findById(adminId);
        solicitud.setEstado(Solicitud.Estado.RECHAZADA);
        solicitud.setAdmin(admin);
        solicitud.setObservaciones(observaciones);
        solicitud.setGestionadaAt(LocalDateTime.now());
        log.info("Solicitud id: {} rechazada correctamente por admin id: {}", solicitudId, adminId);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> findAll() {
        return solicitudRepository.findAll();
    }
}