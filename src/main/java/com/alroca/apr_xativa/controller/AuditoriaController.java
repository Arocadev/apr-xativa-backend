package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.entity.AuditoriaLog;
import com.alroca.apr_xativa.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<Page<AuditoriaLog>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditoriaService.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        ));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AuditoriaLog>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(auditoriaService.findByUsuario(usuarioId));
    }
}