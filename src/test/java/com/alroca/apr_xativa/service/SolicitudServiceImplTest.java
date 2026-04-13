package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Solicitud;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.SolicitudNotFoundException;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceImplTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private SolicitudServiceImpl solicitudService;

    private Usuario usuario;
    private Usuario admin;
    private Solicitud solicitud;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setDni("12345678B");

        admin = new Usuario();
        admin.setId(1L);
        admin.setDni("00000001A");
        admin.setRol(Usuario.Rol.ADMIN);

        solicitud = new Solicitud();
        solicitud.setId(1L);
        solicitud.setUsuario(usuario);
        solicitud.setEstado(Solicitud.Estado.PENDIENTE);
    }

    @Test
    void crear_cuandoNoHayPendiente_creaSolicitud() {
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.PENDIENTE))
                .thenReturn(Optional.empty());
        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(solicitudRepository.save(any())).thenReturn(solicitud);

        Solicitud resultado = solicitudService.crear(2L);

        assertNotNull(resultado);
        assertEquals(Solicitud.Estado.PENDIENTE, resultado.getEstado());
        verify(solicitudRepository).save(any());
    }

    @Test
    void crear_cuandoYaHayPendiente_lanzaExcepcion() {
        when(solicitudRepository.findByUsuarioIdAndEstado(2L, Solicitud.Estado.PENDIENTE))
                .thenReturn(Optional.of(solicitud));

        assertThrows(DuplicadoException.class, () ->
                solicitudService.crear(2L)
        );
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void findByUsuario_devuelveLista() {
        when(solicitudRepository.findByUsuarioId(2L)).thenReturn(List.of(solicitud));

        List<Solicitud> resultado = solicitudService.findByUsuario(2L);

        assertEquals(1, resultado.size());
        assertEquals(Solicitud.Estado.PENDIENTE, resultado.get(0).getEstado());
    }

    @Test
    void findPendientes_devuelveLista() {
        when(solicitudRepository.findByEstado(Solicitud.Estado.PENDIENTE))
                .thenReturn(List.of(solicitud));

        List<Solicitud> resultado = solicitudService.findPendientes();

        assertEquals(1, resultado.size());
    }

    @Test
    void aprobar_cuandoExiste_apruebaSolicitud() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioService.findById(1L)).thenReturn(admin);
        when(solicitudRepository.save(any())).thenReturn(solicitud);

        Solicitud resultado = solicitudService.aprobar(1L, 1L);

        assertEquals(Solicitud.Estado.APROBADA, resultado.getEstado());
        assertEquals(admin, resultado.getAdmin());
        assertNotNull(resultado.getGestionadaAt());
    }

    @Test
    void aprobar_cuandoNoExiste_lanzaExcepcion() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SolicitudNotFoundException.class, () ->
                solicitudService.aprobar(99L, 1L)
        );
    }

    @Test
    void rechazar_cuandoExiste_rechazaSolicitud() {
        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(usuarioService.findById(1L)).thenReturn(admin);
        when(solicitudRepository.save(any())).thenReturn(solicitud);

        Solicitud resultado = solicitudService.rechazar(1L, 1L, "Documentacion incorrecta");

        assertEquals(Solicitud.Estado.RECHAZADA, resultado.getEstado());
        assertEquals("Documentacion incorrecta", resultado.getObservaciones());
        assertNotNull(resultado.getGestionadaAt());
    }

    @Test
    void rechazar_cuandoNoExiste_lanzaExcepcion() {
        when(solicitudRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SolicitudNotFoundException.class, () ->
                solicitudService.rechazar(99L, 1L, "observaciones")
        );
    }
}