package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.entity.Documento;
import com.alroca.apr_xativa.repository.DocumentoRepository;
import com.alroca.apr_xativa.service.UsuarioService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoRepository documentoRepository;
    private final UsuarioService usuarioService;
    private final SecurityUtils securityUtils;

    @PostMapping("/subir")
    public ResponseEntity<Void> subirDocumento(
            @RequestParam("archivo") MultipartFile archivo) throws IOException {
        Long usuarioId = securityUtils.getUsuarioAutenticado().getId();
        String dni = usuarioService.findById(usuarioId).getDni();
        String extension = "";
        String originalFilename = archivo.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String nombreFinal = dni + extension;
        String ruta = "uploads/" + nombreFinal;
        Path path = Paths.get(ruta);
        Files.createDirectories(path.getParent());
        Files.write(path, archivo.getBytes());

        Documento documento = new Documento();
        documento.setUsuario(usuarioService.findById(usuarioId));
        documento.setTipoDoc("empadronamiento");
        documento.setRuta(ruta);
        documentoRepository.save(documento);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Documento>> getDocumentosByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(documentoRepository.findByUsuarioId(usuarioId));
    }

    @GetMapping("/ver/{documentoId}")
    public ResponseEntity<Resource> verDocumento(@PathVariable Long documentoId) throws IOException {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
        Path path = Paths.get(documento.getRuta());
        Resource resource = new FileSystemResource(path);
        String contentType = Files.probeContentType(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName() + "\"")
                .body(resource);
    }
}