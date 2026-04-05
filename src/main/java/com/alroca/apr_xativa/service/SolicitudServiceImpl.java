package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Solicitud;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.SolicitudNotFoundException;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;

    @Override
    public Solicitud crear(Long usuarioId) {
        solicitudRepository.findByUsuarioIdAndEstado(usuarioId, Solicitud.Estado.PENDIENTE)
                .ifPresent(s -> { throw new DuplicadoException("Ya tienes una solicitud pendiente"); });

        Usuario usuario = usuarioService.findById(usuarioId);
        Solicitud solicitud = Solicitud.builder()
                .usuario(usuario)
                .estado(Solicitud.Estado.PENDIENTE)
                .build();
        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> findByUsuario(Long usuarioId) {
        return solicitudRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public List<Solicitud> findPendientes() {
        return solicitudRepository.findByEstado(Solicitud.Estado.PENDIENTE);
    }

    @Override
    public Solicitud aprobar(Long solicitudId, Long adminId) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));
        Usuario admin = usuarioService.findById(adminId);
        solicitud.setEstado(Solicitud.Estado.APROBADA);
        solicitud.setAdmin(admin);
        solicitud.setGestionadaAt(LocalDateTime.now());
        return solicitudRepository.save(solicitud);
    }

    @Override
    public Solicitud rechazar(Long solicitudId, Long adminId, String observaciones) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudNotFoundException(solicitudId));
        Usuario admin = usuarioService.findById(adminId);
        solicitud.setEstado(Solicitud.Estado.RECHAZADA);
        solicitud.setAdmin(admin);
        solicitud.setObservaciones(observaciones);
        solicitud.setGestionadaAt(LocalDateTime.now());
        return solicitudRepository.save(solicitud);
    }
}