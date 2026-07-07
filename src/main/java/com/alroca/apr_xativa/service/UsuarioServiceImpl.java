package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.AuditoriaLog;
import com.alroca.apr_xativa.entity.Solicitud;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.UsuarioNotFoundException;
import com.alroca.apr_xativa.repository.SolicitudRepository;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import com.alroca.apr_xativa.utils.ValidacionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SolicitudRepository solicitudRepository;
    private final AuditoriaService auditoriaService;

    @Override
    public Usuario findByEmail(String email) {
        log.debug("Buscando usuario por email: {}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }

    @Override
    public Usuario findByDni(String dni) {
        log.debug("Buscando usuario por DNI: {}", dni);
        return usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsuarioNotFoundException(dni));
    }

    @Override
    public Usuario findById(Long id) {
        log.debug("Buscando usuario por id: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("id: " + id));
    }

    @Override
    public List<Usuario> findAll() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll();
    }

    @Override
    public Page<Usuario> findAllPaginado(Pageable pageable) {
        log.info("Listando usuarios paginados: pagina={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        log.info("Intentando registrar usuario con DNI: {}", usuario.getDni());
        if (!ValidacionUtils.esDniValido(usuario.getDni())) {
            log.warn("DNI invalido: {}", usuario.getDni());
            throw new IllegalArgumentException("Formato de DNI o NIE incorrecto: " + usuario.getDni());
        }
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            log.warn("DNI duplicado: {}", usuario.getDni());
            throw new DuplicadoException("Ya existe un usuario con el DNI: " + usuario.getDni());
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            log.warn("Email duplicado: {}", usuario.getEmail());
            throw new DuplicadoException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(Usuario.Rol.USER);
        usuario.setActivo(false);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Solicitud solicitud = new Solicitud();
        solicitud.setUsuario(usuarioGuardado);
        solicitud.setEstado(Solicitud.Estado.PENDIENTE);
        solicitudRepository.save(solicitud);

        auditoriaService.registrar(AuditoriaLog.Evento.REGISTRO_USUARIO, usuarioGuardado,
                "Nuevo registro: " + usuarioGuardado.getDni());

        log.info("Usuario registrado correctamente con DNI: {}", usuario.getDni());
        return usuarioGuardado;
    }

    @Override
    public Usuario registrarDesdeAdmin(Usuario usuario) {
        log.info("Registrando usuario desde admin con DNI: {}", usuario.getDni());
        if (!ValidacionUtils.esDniValido(usuario.getDni())) {
            throw new IllegalArgumentException("Formato de DNI o NIE incorrecto: " + usuario.getDni());
        }
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            throw new DuplicadoException("Ya existe un usuario con el DNI: " + usuario.getDni());
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new DuplicadoException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(Usuario.Rol.USER);
        usuario.setActivo(true);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        auditoriaService.registrar(AuditoriaLog.Evento.REGISTRO_USUARIO, usuarioGuardado,
                "Registro desde admin: " + usuarioGuardado.getDni());

        return usuarioGuardado;
    }

    @Override
    public void desactivar(Long id) {
        log.info("Desactivando usuario con id: {}", id);
        Usuario usuario = findById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado correctamente con id: {}", id);
    }

    @Override
    public void reactivar(Long id) {
        log.info("Reactivando usuario con id: {}", id);
        Usuario usuario = findById(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
        log.info("Usuario reactivado correctamente con id: {}", id);
    }

    @Override
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        Usuario usuario = findById(id);
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta");
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
        log.info("Contraseña cambiada correctamente para usuario id: {}", id);
    }
}