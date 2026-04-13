package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.dto.VehiculoRequestDTO;
import com.alroca.apr_xativa.dto.VehiculoResponseDTO;
import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.entity.Vehiculo;
import com.alroca.apr_xativa.mapper.VehiculoMapper;
import com.alroca.apr_xativa.service.VehiculoService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;
    private final VehiculoMapper vehiculoMapper;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<VehiculoResponseDTO>> listar() {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(
                vehiculoService.findByUsuario(usuarioId).stream()
                        .map(vehiculoMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping
    public ResponseEntity<VehiculoResponseDTO> alta(@Valid @RequestBody VehiculoRequestDTO request) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        return ResponseEntity.ok(vehiculoMapper.toResponse(
                vehiculoService.alta(usuarioId, request.getMatricula(),
                        Vehiculo.TipoAcred.valueOf(request.getTipoAcred()))
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> baja(@PathVariable Long id) {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        vehiculoService.baja(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}