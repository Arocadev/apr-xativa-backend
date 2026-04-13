package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import com.alroca.apr_xativa.entity.Solicitud;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.entity.Vehiculo;
import com.alroca.apr_xativa.exception.AccesoNoPermitidoException;
import com.alroca.apr_xativa.exception.DerechoAccesoNotFoundException;
import com.alroca.apr_xativa.exception.FechaNoValidaException;
import com.alroca.apr_xativa.exception.VehiculoNotFoundException;
import com.alroca.apr_xativa.repository.DerechoAccesoRepository;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import com.alroca.apr_xativa.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DerechoAccesoServiceImpl implements DerechoAccesoService {

    private final DerechoAccesoRepository derechoAccesoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;

    @Override
    public List<DerechoAcceso> findByUsuario(Long usuarioId) {
        log.debug("Listando derechos de acceso del usuario id: {}", usuarioId);
        return derechoAccesoRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public DerechoAcceso crearPermanente(Long usuarioId, Long vehiculoId) {
        log.info("Creando derecho permanente para usuario id: {} vehiculo id: {}", usuarioId, vehiculoId);
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));

        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
            log.warn("Usuario id: {} intento crear derecho sobre vehiculo id: {} que no le pertenece", usuarioId, vehiculoId);
            throw new AccesoNoPermitidoException("El vehiculo no pertenece a este usuario");
        }

        DerechoAcceso derecho = DerechoAcceso.builder()
                .usuario(usuario)
                .vehiculo(vehiculo)
                .tipoDerecho(DerechoAcceso.TipoDerecho.PERMANENTE)
                .tipoAcred(vehiculo.getTipoAcred())
                .fechaInicio(LocalDate.now())
                .fechaFin(LocalDate.now().plusYears(1))
                .activo(true)
                .build();

        log.info("Derecho permanente creado correctamente para usuario id: {} vehiculo id: {}", usuarioId, vehiculoId);
        return derechoAccesoRepository.save(derecho);
    }

    @Override
    public DerechoAcceso crearPuntual(Long usuarioId, Long vehiculoId, LocalDate fecha) {
        log.info("Creando derecho puntual para usuario id: {} vehiculo id: {} fecha: {}", usuarioId, vehiculoId, fecha);
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);

        if (fecha.isBefore(LocalDate.now().minusDays(5)) ||
                fecha.isAfter(LocalDate.now().plusDays(90))) {
            log.warn("Fecha invalida para derecho puntual: {}", fecha);
            throw new FechaNoValidaException("La fecha debe estar entre 5 dias antes y 90 dias despues de hoy");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));

        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
            log.warn("Usuario id: {} intento crear derecho sobre vehiculo id: {} que no le pertenece", usuarioId, vehiculoId);
            throw new AccesoNoPermitidoException("El vehiculo no pertenece a este usuario");
        }

        DerechoAcceso derecho = DerechoAcceso.builder()
                .usuario(usuario)
                .vehiculo(vehiculo)
                .tipoDerecho(DerechoAcceso.TipoDerecho.PUNTUAL)
                .tipoAcred(vehiculo.getTipoAcred())
                .fechaInicio(fecha)
                .fechaFin(fecha)
                .activo(true)
                .build();

        log.info("Derecho puntual creado correctamente para usuario id: {} vehiculo id: {} fecha: {}", usuarioId, vehiculoId, fecha);
        return derechoAccesoRepository.save(derecho);
    }

    @Override
    public void eliminar(Long derechoId, Long usuarioId) {
        log.info("Intentando eliminar derecho id: {} del usuario id: {}", derechoId, usuarioId);
        DerechoAcceso derecho = derechoAccesoRepository.findById(derechoId)
                .orElseThrow(() -> new DerechoAccesoNotFoundException(derechoId));

        if (!derecho.getUsuario().getId().equals(usuarioId)) {
            log.warn("Usuario id: {} intento eliminar derecho id: {} que no le pertenece", usuarioId, derechoId);
            throw new AccesoNoPermitidoException("No tienes permiso para eliminar este derecho");
        }

        if (derecho.getTipoAcred() == Vehiculo.TipoAcred.ACREDITADO) {
            log.warn("Intento de eliminar derecho acreditado id: {}", derechoId);
            throw new AccesoNoPermitidoException("No se pueden eliminar derechos de tipo acreditado");
        }

        derecho.setActivo(false);
        derechoAccesoRepository.save(derecho);
        log.info("Derecho id: {} eliminado correctamente", derechoId);
    }

    private void validarSolicitudAprobada(Long usuarioId) {
        solicitudRepository.findByUsuarioIdAndEstado(usuarioId, Solicitud.Estado.APROBADA)
                .orElseThrow(() -> {
                    log.warn("Usuario id: {} no tiene solicitud aprobada", usuarioId);
                    return new AccesoNoPermitidoException("El usuario no tiene una solicitud aprobada");
                });
    }
}