package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.VehiculoRequestDTO;
import com.alroca.apr_xativa.dto.VehiculoResponseDTO;
import com.alroca.apr_xativa.mapper.VehiculoMapper;
import com.alroca.apr_xativa.service.UsuarioService;
import com.alroca.apr_xativa.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final UsuarioService usuarioService;
    private final VehiculoMapper vehiculoMapper;

    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listar(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long usuarioId = usuarioService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(
                vehiculoService.findByUsuario(usuarioId).stream()
                        .map(vehiculoMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> alta(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody VehiculoRequestDTO request) {
        Long usuarioId = usuarioService.findByEmail(userDetails.getUsername()).getId();
        return ResponseEntity.ok(vehiculoMapper.toResponse(
                vehiculoService.alta(usuarioId, request.getMatricula(),
                        com.alroca.apr_xativa.entity.Vehiculo.TipoAcred.valueOf(request.getTipoAcred()))
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> baja(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long usuarioId = usuarioService.findByEmail(userDetails.getUsername()).getId();
        vehiculoService.baja(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}