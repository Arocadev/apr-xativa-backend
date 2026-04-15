package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario findByEmail(String email);
    Usuario findByDni(String dni);
    Usuario findById(Long id);
    List<Usuario> findAll();
    Usuario registrar(Usuario usuario);
    void desactivar(Long id);
    void reactivar(Long id);
}
