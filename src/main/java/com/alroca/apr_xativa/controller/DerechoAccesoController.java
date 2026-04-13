package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.DerechoAccesoResponseDTO;
import com.alroca.apr_xativa.mapper.DerechoAccesoMapper;
import com.alroca.apr_xativa.service.DerechoAccesoService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<DerechoAccesoResponseDTO>> listar() {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(
                derechoAccesoService.findByUsuario(usuarioId).stream()
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        derechoAccesoService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}