package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.UsuarioRequestDTO;
import com.alroca.apr_xativa.dto.UsuarioResponseDTO;
import com.alroca.apr_xativa.mapper.UsuarioMapper;
import com.alroca.apr_xativa.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registro(@Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.ok(usuarioMapper.toResponse(
                usuarioService.registrar(usuarioMapper.toEntity(request))
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(usuarioMapper.toResponse(
                usuarioService.findByDni(userDetails.getUsername())
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(
                usuarioService.findAll().stream()
                        .map(usuarioMapper::toResponse)
                        .toList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}