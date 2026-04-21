package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.AuthResponse;
import com.alroca.apr_xativa.dto.LoginRequest;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.repository.UsuarioRepository;
import com.alroca.apr_xativa.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getDni(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getDni());
        String token = jwtService.generateToken(userDetails);

        Usuario usuario = usuarioRepository.findByDni(request.getDni()).orElseThrow();

        return ResponseEntity.ok(new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getRol().name(),
                usuario.isActivo()
        ));
    }
}