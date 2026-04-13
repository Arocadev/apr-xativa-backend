package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.SolicitudResponseDTO;
import com.alroca.apr_xativa.mapper.SolicitudMapper;
import com.alroca.apr_xativa.service.SolicitudService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final SolicitudMapper solicitudMapper;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<SolicitudResponseDTO> crear() {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(solicitudMapper.toResponse(
                solicitudService.crear(usuarioId)
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SolicitudResponseDTO>> misSolicitudes() {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(
                solicitudService.findByUsuario(usuarioId).stream()
                        .map(solicitudMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudResponseDTO>> pendientes() {
        return ResponseEntity.ok(
                solicitudService.findPendientes().stream()
                        .map(solicitudMapper::toResponse)
                        .toList()
        );
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudResponseDTO> aprobar(@PathVariable Long id) {
        Long adminId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(solicitudMapper.toResponse(
                solicitudService.aprobar(id, adminId)
        ));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudResponseDTO> rechazar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        Long adminId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(solicitudMapper.toResponse(
                solicitudService.rechazar(id, adminId, body.get("observaciones"))
        ));
    }
}