package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.UsuarioNotFoundException;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import com.alroca.apr_xativa.utils.ValidacionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException(email));
    }

    @Override
    public Usuario findByDni(String dni) {
        return usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsuarioNotFoundException(dni));
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("id: " + id));
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario registrar(Usuario usuario) {
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
        return usuarioRepository.save(usuario);
    }

    @Override
    public void desactivar(Long id) {
        Usuario usuario = findById(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}