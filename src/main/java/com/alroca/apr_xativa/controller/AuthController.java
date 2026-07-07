package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.AuthResponse;
import com.alroca.apr_xativa.dto.LoginRequest;
import com.alroca.apr_xativa.entity.AuditoriaLog;
import com.alroca.apr_xativa.entity.RefreshToken;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import com.alroca.apr_xativa.security.JwtService;
import com.alroca.apr_xativa.service.AuditoriaService;
import com.alroca.apr_xativa.service.RefreshTokenService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenService refreshTokenService;
    private final SecurityUtils securityUtils;
    private final AuditoriaService auditoriaService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getDni(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getDni());
        String token = jwtService.generateToken(userDetails);

        Usuario usuario = usuarioRepository.findByDni(request.getDni()).orElseThrow();
        RefreshToken refreshToken = refreshTokenService.crear(usuario);

        auditoriaService.registrar(AuditoriaLog.Evento.LOGIN, usuario, "Login exitoso");

        return ResponseEntity.ok(new AuthResponse(
                token,
                refreshToken.getToken(),
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.isActivo()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> body) {
        String tokenStr = body.get("refreshToken");
        RefreshToken refreshToken = refreshTokenService.validar(tokenStr);

        Usuario usuario = refreshToken.getUsuario();
        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getDni());
        String nuevoJwt = jwtService.generateToken(userDetails);

        RefreshToken nuevoRefresh = refreshTokenService.crear(usuario);

        return ResponseEntity.ok(new AuthResponse(
                nuevoJwt,
                nuevoRefresh.getToken(),
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.isActivo()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Usuario usuario = securityUtils.getUsuarioAutenticado();
        refreshTokenService.revocarTodos(usuario.getId());
        auditoriaService.registrar(AuditoriaLog.Evento.LOGOUT, usuario, "Logout");
        return ResponseEntity.noContent().build();
    }
}