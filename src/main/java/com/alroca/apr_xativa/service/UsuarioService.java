package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UsuarioService {
    Usuario findByEmail(String email);
    Usuario findByDni(String dni);
    Usuario findById(Long id);
    List<Usuario> findAll();
    Page<Usuario> findAllPaginado(Pageable pageable);
    Usuario registrar(Usuario usuario);
    void desactivar(Long id);
    void reactivar(Long id);
    void cambiarPassword(Long id, String passwordActual, String passwordNueva);
    Usuario registrarDesdeAdmin(Usuario usuario);
}