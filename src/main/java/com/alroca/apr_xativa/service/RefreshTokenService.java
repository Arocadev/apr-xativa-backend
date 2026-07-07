package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.RefreshToken;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.exception.AccesoNoPermitidoException;
import com.alroca.apr_xativa.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_DIAS = 30;

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken crear(Usuario usuario) {
        refreshTokenRepository.revocarTodosPorUsuario(usuario.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .usuario(usuario)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_TOKEN_DIAS))
                .revocado(false)
                .build();

        log.info("Refresh token creado para usuario id: {}", usuario.getId());
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validar(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AccesoNoPermitidoException("Refresh token no válido"));

        if (refreshToken.isRevocado()) {
            throw new AccesoNoPermitidoException("Refresh token revocado");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AccesoNoPermitidoException("Refresh token expirado");
        }

        return refreshToken;
    }

    @Transactional
    public void revocarTodos(Long usuarioId) {
        refreshTokenRepository.revocarTodosPorUsuario(usuarioId);
        log.info("Refresh tokens revocados para usuario id: {}", usuarioId);
    }
}