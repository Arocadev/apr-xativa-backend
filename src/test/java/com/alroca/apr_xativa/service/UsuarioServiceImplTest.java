package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.UsuarioNotFoundException;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.alroca.apr_xativa.entity.Solicitud;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SolicitudRepository solicitudRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setDni("12345678B");
        usuario.setNombre("Carlos");
        usuario.setApellidos("Garcia Lopez");
        usuario.setEmail("carlos@email.com");
        usuario.setPassword("password123");
        usuario.setTipo("A.1");
        usuario.setRol(Usuario.Rol.USER);
        usuario.setActivo(true);
    }

    @Test
    void findByDni_cuandoExiste_devuelveUsuario() {
        when(usuarioRepository.findByDni("12345678B")).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findByDni("12345678B");

        assertNotNull(resultado);
        assertEquals("12345678B", resultado.getDni());
        verify(usuarioRepository).findByDni("12345678B");
    }

    @Test
    void findByDni_cuandoNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findByDni("99999999Z")).thenReturn(Optional.empty());

        assertThrows(UsuarioNotFoundException.class, () ->
                usuarioService.findByDni("99999999Z")
        );
    }

    @Test
    void registrar_cuandoDniValido_guardaUsuario() {
        when(usuarioRepository.existsByDni("12345678B")).thenReturn(false);
        when(usuarioRepository.existsByEmail("carlos@email.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(usuarioRepository.save(any())).thenReturn(usuario);
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = usuarioService.registrar(usuario);

        assertNotNull(resultado);
        verify(usuarioRepository).save(any());
        verify(solicitudRepository).save(any(Solicitud.class));
    }

    @Test
    void registrar_cuandoDniDuplicado_lanzaExcepcion() {
        when(usuarioRepository.existsByDni("12345678B")).thenReturn(true);

        assertThrows(DuplicadoException.class, () ->
                usuarioService.registrar(usuario)
        );
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void registrar_cuandoDniInvalido_lanzaExcepcion() {
        usuario.setDni("1234");

        assertThrows(IllegalArgumentException.class, () ->
                usuarioService.registrar(usuario)
        );
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void desactivar_cuandoExiste_desactivaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        usuarioService.desactivar(1L);

        assertFalse(usuario.isActivo());
        verify(usuarioRepository).save(usuario);
    }
}