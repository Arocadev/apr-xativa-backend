package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.AuditoriaLog;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.repository.AuditoriaLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @Async
    public void registrar(AuditoriaLog.Evento evento, Usuario usuario, String detalle, Usuario realizadoPor) {
        try {
            AuditoriaLog entry = AuditoriaLog.builder()
                    .evento(evento)
                    .usuarioId(usuario != null ? usuario.getId() : null)
                    .usuarioDni(usuario != null ? usuario.getDni() : null)
                    .detalle(detalle)
                    .realizadoPorId(realizadoPor != null ? realizadoPor.getId() : null)
                    .realizadoPorDni(realizadoPor != null ? realizadoPor.getDni() : null)
                    .build();
            auditoriaLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Error al registrar auditoria: {}", e.getMessage());
        }
    }

    @Async
    public void registrar(AuditoriaLog.Evento evento, Usuario usuario, String detalle) {
        try {
            AuditoriaLog entry = AuditoriaLog.builder()
                    .evento(evento)
                    .usuarioId(usuario != null ? usuario.getId() : null)
                    .usuarioDni(usuario != null ? usuario.getDni() : null)
                    .detalle(detalle)
                    .realizadoPorId(usuario != null ? usuario.getId() : null)
                    .realizadoPorDni(usuario != null ? usuario.getDni() : null)
                    .build();
            auditoriaLogRepository.save(entry);
        } catch (Exception e) {
            log.error("Error al registrar auditoria: {}", e.getMessage());
        }
    }

    public List<AuditoriaLog> findByUsuario(Long usuarioId) {
        return auditoriaLogRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
    }

    public Page<AuditoriaLog> findAll(Pageable pageable) {
        return auditoriaLogRepository.findAll(pageable);
    }
}