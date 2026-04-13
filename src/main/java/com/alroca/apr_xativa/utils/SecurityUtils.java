package com.alroca.apr_xativa.utils;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.UsuarioNotFoundException;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public Usuario getUsuarioAutenticado() {
        String dni = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return usuarioRepository.findByDni(dni)
                .orElseThrow(() -> new UsuarioNotFoundException(dni));
    }
}