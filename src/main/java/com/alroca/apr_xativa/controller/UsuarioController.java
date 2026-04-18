package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.UsuarioRequestDTO;
import com.alroca.apr_xativa.dto.UsuarioResponseDTO;
import com.alroca.apr_xativa.mapper.UsuarioMapper;
import com.alroca.apr_xativa.service.UsuarioService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final SecurityUtils securityUtils;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registro(@Valid @RequestBody UsuarioRequestDTO request) {
        return ResponseEntity.ok(usuarioMapper.toResponse(
                usuarioService.registrar(usuarioMapper.toEntity(request))
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me() {
        return ResponseEntity.ok(usuarioMapper.toResponse(
                securityUtils.getUsuarioAutenticado()
        ));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(
                usuarioService.findAll().stream()
                        .map(usuarioMapper::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivar(@PathVariable Long id) {
        usuarioService.reactivar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(
            @RequestBody Map<String, String> body) {
        usuarioService.cambiarPassword(
                securityUtils.getUsuarioAutenticado().getId(),
                body.get("passwordActual"),
                body.get("passwordNueva")
        );
        return ResponseEntity.noContent().build();
    }
}