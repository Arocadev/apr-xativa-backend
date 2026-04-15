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
    public ResponseEntity<List<VehiculoResponseDTO>> listar(
            @RequestParam(required = false) Long usuarioId) {
        Long id = usuarioId != null ? usuarioId : securityUtils.getUsuarioAutenticado().getId();
        List<Vehiculo> vehiculos = usuarioId != null
                ? vehiculoService.findAllByUsuario(id)
                : vehiculoService.findByUsuario(id);
        return ResponseEntity.ok(
                vehiculos.stream()
                        .map(vehiculoMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/todos")
    public ResponseEntity<List<VehiculoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(
                vehiculoService.findAll().stream()
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