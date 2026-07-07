package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.DerechoAccesoResponseDTO;
import com.alroca.apr_xativa.mapper.DerechoAccesoMapper;
import com.alroca.apr_xativa.service.DerechoAccesoService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/derechos")
@RequiredArgsConstructor
public class DerechoAccesoController {

    private final DerechoAccesoService derechoAccesoService;
    private final DerechoAccesoMapper derechoAccesoMapper;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Long id = usuarioId != null ? usuarioId : securityUtils.getUsuarioAutenticado().getId();
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<DerechoAccesoResponseDTO> resultado = derechoAccesoService.findByUsuarioPaginado(id, pageable)
                    .map(derechoAccesoMapper::toResponse);
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.ok(
                derechoAccesoService.findByUsuario(id).stream()
                        .map(derechoAccesoMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/permanente")
    public ResponseEntity<DerechoAccesoResponseDTO> crearPermanente(
            @RequestBody Map<String, Long> body) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(derechoAccesoMapper.toResponse(
                derechoAccesoService.crearPermanente(usuarioId, body.get("vehiculoId"))
        ));
    }

    @PostMapping("/puntual")
    public ResponseEntity<DerechoAccesoResponseDTO> crearPuntual(
            @RequestBody Map<String, String> body) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        Long vehiculoId = Long.parseLong(body.get("vehiculoId"));
        LocalDate fecha = LocalDate.parse(body.get("fecha"));
        return ResponseEntity.ok(derechoAccesoMapper.toResponse(
                derechoAccesoService.crearPuntual(usuarioId, vehiculoId, fecha)
        ));
    }

    @PostMapping("/puntual/invitado")
    public ResponseEntity<DerechoAccesoResponseDTO> crearPuntualInvitado(
            @RequestBody Map<String, String> body) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        String matricula = body.get("matricula");
        LocalDate fecha = LocalDate.parse(body.get("fecha"));
        return ResponseEntity.ok(derechoAccesoMapper.toResponse(
                derechoAccesoService.crearPuntualInvitado(usuarioId, matricula, fecha)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        derechoAccesoService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}