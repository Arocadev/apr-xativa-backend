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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DerechoAccesoServiceImplTest {

    @Mock
    private DerechoAccesoRepository derechoAccesoRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private DerechoAccesoServiceImpl derechoAccesoService;

    private Usuario usuario;
    private Vehiculo vehiculo;
    private DerechoAcceso derecho;
    private Solicitud solicitudAprobada;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setDni("12345678B");

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setMatricula("1234ABC");
        vehiculo.setUsuario(usuario);
        vehiculo.setTipoAcred(Vehiculo.TipoAcred.LIBRE);
        vehiculo.setActivo(true);

        derecho = new DerechoAcceso();
        derecho.setId(1L);
        derecho.setUsuario(usuario);
        derecho.setVehiculo(vehiculo);
        derecho.setTipoDerecho(DerechoAcceso.TipoDerecho.PERMANENTE);
        derecho.setTipoAcred(Vehiculo.TipoAcred.LIBRE);
        derecho.setFechaInicio(LocalDate.now());
        derecho.setFechaFin(LocalDate.now().plusYears(1));
        derecho.setActivo(true);

        solicitudAprobada = new Solicitud();
        solicitudAprobada.setId(1L);
        solicitudAprobada.setUsuario(usuario);
        solicitudAprobada.setEstado(Solicitud.Estado.APROBADA);
    }

    @Test
    void findByUsuario_devuelveListaDerechos() {
        when(derechoAccesoRepository.findByUsuarioIdAndActivoTrue(2L)).thenReturn(List.of(derecho));

        List<DerechoAcceso> resultado = derechoAccesoService.findByUsuario(2L);

        assertEquals(1, resultado.size());
        assertEquals(DerechoAcceso.TipoDerecho.PERMANENTE, resultado.get(0).getTipoDerecho());
    }

    @Test
    void crearPermanente_cuandoSolicitudAprobada_creaDerechoU() {
        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.APROBADA))
                .thenReturn(Optional.of(solicitudAprobada));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(derechoAccesoRepository.save(any())).thenReturn(derecho);

        DerechoAcceso resultado = derechoAccesoService.crearPermanente(2L, 1L);

        assertNotNull(resultado);
        assertEquals(DerechoAcceso.TipoDerecho.PERMANENTE, resultado.getTipoDerecho());
        verify(derechoAccesoRepository).save(any());
    }

    @Test
    void crearPermanente_cuandoSinSolicitudAprobada_lanzaExcepcion() {
        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.APROBADA))
                .thenReturn(Optional.empty());

        assertThrows(AccesoNoPermitidoException.class, () ->
                derechoAccesoService.crearPermanente(2L, 1L)
        );
    }

    @Test
    void crearPermanente_cuandoVehiculoNoPertenece_lanzaExcepcion() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(99L);
        vehiculo.setUsuario(otroUsuario);

        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.APROBADA))
                .thenReturn(Optional.of(solicitudAprobada));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        assertThrows(AccesoNoPermitidoException.class, () ->
                derechoAccesoService.crearPermanente(2L, 1L)
        );
    }

    @Test
    void crearPuntual_cuandoFechaValida_creaDerechoPuntual() {
        derecho.setTipoDerecho(DerechoAcceso.TipoDerecho.PUNTUAL);
        LocalDate fecha = LocalDate.now().plusDays(5);

        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.APROBADA))
                .thenReturn(Optional.of(solicitudAprobada));
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(derechoAccesoRepository.save(any())).thenReturn(derecho);

        DerechoAcceso resultado = derechoAccesoService.crearPuntual(2L, 1L, fecha);

        assertNotNull(resultado);
        verify(derechoAccesoRepository).save(any());
    }

    @Test
    void crearPuntual_cuandoFechaFueraDeRango_lanzaExcepcion() {
        LocalDate fechaInvalida = LocalDate.now().plusDays(100);

        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.APROBADA))
                .thenReturn(Optional.of(solicitudAprobada));

        assertThrows(FechaNoValidaException.class, () ->
                derechoAccesoService.crearPuntual(2L, 1L, fechaInvalida)
        );
    }

    @Test
    void eliminar_cuandoDerechoExisteYPertenece_desactiva() {
        when(derechoAccesoRepository.findById(1L)).thenReturn(Optional.of(derecho));
        when(derechoAccesoRepository.save(any())).thenReturn(derecho);

        derechoAccesoService.eliminar(1L, 2L);

        assertFalse(derecho.isActivo());
        verify(derechoAccesoRepository).save(derecho);
    }

    @Test
    void eliminar_cuandoDerechoNoExiste_lanzaExcepcion() {
        when(derechoAccesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DerechoAccesoNotFoundException.class, () ->
                derechoAccesoService.eliminar(99L, 2L)
        );
    }

    @Test
    void eliminar_cuandoDerechoNoPertenece_lanzaExcepcion() {
        when(derechoAccesoRepository.findById(1L)).thenReturn(Optional.of(derecho));

        assertThrows(AccesoNoPermitidoException.class, () ->
                derechoAccesoService.eliminar(1L, 99L)
        );
    }

    @Test
    void eliminar_cuandoDerechoAcreditado_lanzaExcepcion() {
        derecho.setTipoAcred(Vehiculo.TipoAcred.ACREDITADO);
        when(derechoAccesoRepository.findById(1L)).thenReturn(Optional.of(derecho));

        assertThrows(AccesoNoPermitidoException.class, () ->
                derechoAccesoService.eliminar(1L, 2L)
        );
    }

    @Test
    void crearPermanente_cuandoVehiculoNoEncontrado_lanzaExcepcion() {
        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.APROBADA))
                .thenReturn(Optional.of(solicitudAprobada));
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(VehiculoNotFoundException.class, () ->
                derechoAccesoService.crearPermanente(2L, 99L)
        );
    }
}