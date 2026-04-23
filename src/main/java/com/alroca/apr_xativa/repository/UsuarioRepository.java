package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByDni(String dni);
    Optional<Usuario> findByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM Usuario u WHERE u.activo = false " +
            "AND u.createdAt < :fecha " +
            "AND u.id NOT IN (SELECT d.usuario.id FROM Documento d)")
    List<Long> findUsuariosSinDocumentacion(@Param("fecha") LocalDateTime fecha);
}