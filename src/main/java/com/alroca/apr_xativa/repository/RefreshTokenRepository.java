package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.usuario.id = :usuarioId")
    void revocarTodosPorUsuario(@Param("usuarioId") Long usuarioId);
}