package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.entity.Vehiculo;
import com.alroca.apr_xativa.exception.AccesoNoPermitidoException;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.VehiculoNotFoundException;
import com.alroca.apr_xativa.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceImplTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private VehiculoServiceImpl vehiculoService;

    private Usuario usuario;
    private Vehiculo vehiculo;

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
    }

    @Test
    void findByUsuario_devuelveListaVehiculos() {
        when(vehiculoRepository.findByUsuarioIdAndActivoTrue(2L)).thenReturn(List.of(vehiculo));

        List<Vehiculo> resultado = vehiculoService.findByUsuario(2L);

        assertEquals(1, resultado.size());
        assertEquals("1234ABC", resultado.get(0).getMatricula());
    }

    @Test
    void alta_cuandoMatriculaValida_guardaVehiculo() {
        when(vehiculoRepository.existsByMatriculaAndUsuarioId("1234ABC", 2L)).thenReturn(false);
        when(usuarioService.findById(2L)).thenReturn(usuario);
        when(vehiculoRepository.save(any())).thenReturn(vehiculo);

        Vehiculo resultado = vehiculoService.alta(2L, "1234ABC", Vehiculo.TipoAcred.LIBRE);

        assertNotNull(resultado);
        verify(vehiculoRepository).save(any());
    }

    @Test
    void alta_cuandoMatriculaInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                vehiculoService.alta(2L, "MAL", Vehiculo.TipoAcred.LIBRE)
        );
        verify(vehiculoRepository, never()).save(any());
    }

    @Test
    void alta_cuandoMatriculaDuplicada_lanzaExcepcion() {
        when(vehiculoRepository.existsByMatriculaAndUsuarioId("1234ABC", 2L)).thenReturn(true);

        assertThrows(DuplicadoException.class, () ->
                vehiculoService.alta(2L, "1234ABC", Vehiculo.TipoAcred.LIBRE)
        );
        verify(vehiculoRepository, never()).save(any());
    }

    @Test
    void baja_cuandoVehiculoExisteYPertenece_desactiva() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any())).thenReturn(vehiculo);

        vehiculoService.baja(1L, 2L);

        assertFalse(vehiculo.isActivo());
        verify(vehiculoRepository).save(vehiculo);
    }

    @Test
    void baja_cuandoVehiculoNoExiste_lanzaExcepcion() {
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(VehiculoNotFoundException.class, () ->
                vehiculoService.baja(99L, 2L)
        );
    }

    @Test
    void baja_cuandoVehiculoNoPertenece_lanzaExcepcion() {
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        assertThrows(AccesoNoPermitidoException.class, () ->
                vehiculoService.baja(1L, 99L)
        );
    }
}