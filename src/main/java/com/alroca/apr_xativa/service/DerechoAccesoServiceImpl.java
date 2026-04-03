package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import com.alroca.apr_xativa.entity.Solicitud;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.entity.Vehiculo;
import com.alroca.apr_xativa.repository.DerechoAccesoRepository;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import com.alroca.apr_xativa.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DerechoAccesoServiceImpl implements DerechoAccesoService {

    private final DerechoAccesoRepository derechoAccesoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;

    @Override
    public List<DerechoAcceso> findByUsuario(Long usuarioId) {
        return derechoAccesoRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public DerechoAcceso crearPermanente(Long usuarioId, Long vehiculoId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("El vehículo no pertenece a este usuario");
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

        return derechoAccesoRepository.save(derecho);
    }

    @Override
    public DerechoAcceso crearPuntual(Long usuarioId, Long vehiculoId, LocalDate fecha) {
        Usuario usuario = usuarioService.findById(usuarioId);
        validarSolicitudAprobada(usuarioId);

        if (fecha.isBefore(LocalDate.now().minusDays(5)) ||
                fecha.isAfter(LocalDate.now().plusDays(90))) {
            throw new RuntimeException("La fecha debe estar entre 5 dias antes y 90 dias despues de hoy");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("El vehículo no pertenece a este usuario");
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

        return derechoAccesoRepository.save(derecho);
    }

    @Override
    public void eliminar(Long derechoId, Long usuarioId) {
        DerechoAcceso derecho = derechoAccesoRepository.findById(derechoId)
                .orElseThrow(() -> new RuntimeException("Derecho no encontrado"));

        if (!derecho.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este derecho");
        }

        if (derecho.getTipoAcred() == Vehiculo.TipoAcred.ACREDITADO) {
            throw new RuntimeException("No se pueden eliminar derechos de tipo acreditado");
        }

        derecho.setActivo(false);
        derechoAccesoRepository.save(derecho);
    }

    private void validarSolicitudAprobada(Long usuarioId) {
        solicitudRepository.findByUsuarioIdAndEstado(usuarioId, Solicitud.Estado.APROBADA)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene una solicitud aprobada"));
    }
}
