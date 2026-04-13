package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.UsuarioNotFoundException;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import com.alroca.apr_xativa.utils.ValidacionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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
        usuario.setActivo(true);
        log.info("Usuario registrado correctamente con DNI: {}", usuario.getDni());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void desactivar(Long id) {
        log.info("Desactivando usuario con id: {}", id);
        Usuario usuario = findById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
        log.info("Usuario desactivado correctamente con id: {}", id);
    }
}