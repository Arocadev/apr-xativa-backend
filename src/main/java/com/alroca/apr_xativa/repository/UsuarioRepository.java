package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
}
