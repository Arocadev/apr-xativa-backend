package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.SolicitudResponseDTO;
import com.alroca.apr_xativa.mapper.SolicitudMapper;
import com.alroca.apr_xativa.service.SolicitudService;
import com.alroca.apr_xativa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final UsuarioService usuarioService;
    private final SolicitudMapper solicitudMapper;

    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crear(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long usuarioId = usuarioService.findByDni(userDetails.getUsername()).getId();
        return ResponseEntity.ok(solicitudMapper.toResponse(
                solicitudService.crear(usuarioId)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SolicitudResponseDTO>> misSolicitudes(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long usuarioId = usuarioService.findByDni(userDetails.getUsername()).getId();
        return ResponseEntity.ok(
                solicitudService.findByUsuario(usuarioId).stream()
                        .map(solicitudMapper::toResponse)
                        .toList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudResponseDTO>> pendientes() {
        return ResponseEntity.ok(
                solicitudService.findPendientes().stream()
                        .map(solicitudMapper::toResponse)
                        .toList()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudResponseDTO> aprobar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long adminId = usuarioService.findByDni(userDetails.getUsername()).getId();
        return ResponseEntity.ok(solicitudMapper.toResponse(
                solicitudService.aprobar(id, adminId)
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudResponseDTO> rechazar(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        Long adminId = usuarioService.findByDni(userDetails.getUsername()).getId();
        return ResponseEntity.ok(solicitudMapper.toResponse(
                solicitudService.rechazar(id, adminId, body.get("observaciones"))
        ));
    }
}