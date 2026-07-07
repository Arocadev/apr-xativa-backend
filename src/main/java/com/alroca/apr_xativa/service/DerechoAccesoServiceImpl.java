package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.AuditoriaLog;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DerechoAccesoServiceImpl implements DerechoAccesoService {

    private static final int MAX_INVITACIONES_MES = 5;

    private final DerechoAccesoRepository derechoAccesoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;

    @Override
    public List<DerechoAcceso> findByUsuario(Long usuarioId) {
        log.debug("Listando derechos de acceso del usuario id: {}", usuarioId);
        return derechoAccesoRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public Page<DerechoAcceso> findByUsuarioPaginado(Long usuarioId, Pageable pageable) {
        log.debug("Listando derechos paginados del usuario id: {}", usuarioId);
        return derechoAccesoRepository.findByUsuarioIdAndActivoTrue(usuarioId, pageable);
    }

    @Override
    public DerechoAcceso crearPermanente(Long usuarioId, Long vehiculoId) {
        log.info("Creando derecho permanente para usuario id: {} vehiculo id: {}", usuarioId, vehiculoId);
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));

        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
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

        DerechoAcceso guardado = derechoAccesoRepository.save(derecho);

        auditoriaService.registrar(AuditoriaLog.Evento.DERECHO_PERMANENTE_CREADO, usuario,
                "Derecho permanente creado para vehículo: " + vehiculo.getMatricula());

        log.info("Derecho permanente creado para usuario id: {} vehiculo id: {}", usuarioId, vehiculoId);
        return guardado;
    }

    @Override
    public DerechoAcceso crearPuntual(Long usuarioId, Long vehiculoId, LocalDate fecha) {
        log.info("Creando derecho puntual para usuario id: {} vehiculo id: {} fecha: {}", usuarioId, vehiculoId, fecha);
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);
        validarFecha(fecha);

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));

        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
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

        DerechoAcceso guardado = derechoAccesoRepository.save(derecho);

        auditoriaService.registrar(AuditoriaLog.Evento.DERECHO_PUNTUAL_CREADO, usuario,
                "Derecho puntual creado per a " + vehiculo.getMatricula() + " el " + fecha);

        return guardado;
    }

    @Override
    public DerechoAcceso crearPuntualInvitado(Long usuarioId, String matricula, LocalDate fecha) {
        log.info("Creando derecho puntual invitado para usuario id: {} matricula: {} fecha: {}", usuarioId, matricula, fecha);
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);
        validarFecha(fecha);
        validarLimiteInvitaciones(usuarioId, fecha);

        DerechoAcceso derecho = DerechoAcceso.builder()
                .usuario(usuario)
                .vehiculo(null)
                .matriculaInvitado(matricula.toUpperCase())
                .tipoDerecho(DerechoAcceso.TipoDerecho.PUNTUAL)
                .tipoAcred(Vehiculo.TipoAcred.LIBRE)
                .fechaInicio(fecha)
                .fechaFin(fecha)
                .activo(true)
                .build();

        DerechoAcceso guardado = derechoAccesoRepository.save(derecho);

        auditoriaService.registrar(AuditoriaLog.Evento.DERECHO_INVITADO_CREADO, usuario,
                "Derecho invitado creat per a " + matricula.toUpperCase() + " el " + fecha);

        log.info("Derecho puntual invitado creado para matricula: {} fecha: {}", matricula, fecha);
        return guardado;
    }

    @Override
    public void eliminar(Long derechoId, Long usuarioId) {
        log.info("Eliminando derecho id: {} del usuario id: {}", derechoId, usuarioId);
        DerechoAcceso derecho = derechoAccesoRepository.findById(derechoId)
                .orElseThrow(() -> new DerechoAccesoNotFoundException(derechoId));

        if (!derecho.getUsuario().getId().equals(usuarioId)) {
            throw new AccesoNoPermitidoException("No tienes permiso para eliminar este derecho");
        }

        if (derecho.getTipoAcred() == Vehiculo.TipoAcred.ACREDITADO) {
            throw new AccesoNoPermitidoException("No se pueden eliminar derechos de tipo acreditado");
        }

        derecho.setActivo(false);
        derechoAccesoRepository.save(derecho);

        auditoriaService.registrar(AuditoriaLog.Evento.DERECHO_ELIMINADO, derecho.getUsuario(),
                "Derecho id " + derechoId + " eliminat");

        log.info("Derecho id: {} eliminado correctamente", derechoId);
    }

    private void validarSolicitudAprobada(Long usuarioId) {
        solicitudRepository.findByUsuarioIdAndEstado(usuarioId, Solicitud.Estado.APROBADA)
                .orElseThrow(() -> {
                    log.warn("Usuario id: {} no tiene solicitud aprobada", usuarioId);
                    return new AccesoNoPermitidoException("El usuario no tiene una solicitud aprobada");
                });
    }

    private void validarFecha(LocalDate fecha) {
        LocalDate hoy = LocalDate.now();
        LocalDate ultimoDiaMesSiguiente = hoy.plusMonths(1).withDayOfMonth(
                hoy.plusMonths(1).lengthOfMonth()
        );
        if (fecha.isBefore(hoy)) {
            throw new FechaNoValidaException("La fecha no puede ser anterior a hoy");
        }
        if (fecha.isAfter(ultimoDiaMesSiguiente)) {
            throw new FechaNoValidaException("La fecha no puede ser posterior al último día del mes siguiente");
        }
    }

    private void validarLimiteInvitaciones(Long usuarioId, LocalDate fecha) {
        long invitacionesMes = derechoAccesoRepository.countInvitacionesMes(
                usuarioId, fecha.getYear(), fecha.getMonthValue()
        );
        if (invitacionesMes >= MAX_INVITACIONES_MES) {
            throw new AccesoNoPermitidoException(
                    "Has alcanzado el límite de " + MAX_INVITACIONES_MES + " invitaciones para este mes"
            );
        }
        log.info("Usuario id: {} tiene {} invitaciones este mes", usuarioId, invitacionesMes);
    }
}