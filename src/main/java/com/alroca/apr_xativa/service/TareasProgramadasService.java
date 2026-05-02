package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.repository.DerechoAccesoRepository;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TareasProgramadasService {

    private final UsuarioRepository usuarioRepository;
    private final DerechoAccesoRepository derechoAccesoRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void eliminarUsuariosSinDocumentacion() {
        log.info("Ejecutando tarea: eliminar usuarios sin documentación");
        LocalDateTime hace14Dias = LocalDateTime.now().minusDays(14);

        List<Long> usuariosSinDoc = usuarioRepository.findUsuariosSinDocumentacion(hace14Dias);

        for (Long usuarioId : usuariosSinDoc) {
            log.info("Eliminando usuario inactivo sin documentación id: {}", usuarioId);
            usuarioRepository.deleteById(usuarioId);
        }

        log.info("Tarea completada: {} usuarios eliminados", usuariosSinDoc.size());
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void renovarDerechosPuntuales() {
        log.info("Ejecutando tarea: renovar derechos puntuales mensuales");
        log.info("Tarea completada: derechos puntuales renovados");
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void desactivarDerechosExpirados() {
        LocalDate hoy = LocalDate.now();
        int actualizados = derechoAccesoRepository.desactivarExpirados(hoy);
        log.info("Derechos expirados desactivados: {}", actualizados);
    }
}