package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.DerechoAccesoResponseDTO;
import com.alroca.apr_xativa.mapper.DerechoAccesoMapper;
import com.alroca.apr_xativa.service.DerechoAccesoService;
import com.alroca.apr_xativa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/derechos")
@RequiredArgsConstructor
public class DerechoAccesoController {

    private final DerechoAccesoService derechoAccesoService;
    private final UsuarioService usuarioService;
    private final DerechoAccesoMapper derechoAccesoMapper;

    @GetMapping
    public ResponseEntity<List<DerechoAccesoResponseDTO>> listar(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long usuarioId = usuarioService.findByDni(userDetails.getUsername()).getId();
        return ResponseEntity.ok(
                derechoAccesoService.findByUsuario(usuarioId).stream()
                        .map(derechoAccesoMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/permanente")
    public ResponseEntity<DerechoAccesoResponseDTO> crearPermanente(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Long> body) {
        Long usuarioId = usuarioService.findByDni(userDetails.getUsername()).getId();
        return ResponseEntity.ok(derechoAccesoMapper.toResponse(
                derechoAccesoService.crearPermanente(usuarioId, body.get("vehiculoId"))
        ));
    }

    @PostMapping("/puntual")
    public ResponseEntity<DerechoAccesoResponseDTO> crearPuntual(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> body) {
        Long usuarioId = usuarioService.findByDni(userDetails.getUsername()).getId();
        Long vehiculoId = Long.parseLong(body.get("vehiculoId"));
        LocalDate fecha = LocalDate.parse(body.get("fecha"));
        return ResponseEntity.ok(derechoAccesoMapper.toResponse(
                derechoAccesoService.crearPuntual(usuarioId, vehiculoId, fecha)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long usuarioId = usuarioService.findByDni(userDetails.getUsername()).getId();
        derechoAccesoService.eliminar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}