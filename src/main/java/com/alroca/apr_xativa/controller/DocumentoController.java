package com.alroca.apr_xativa.controller;

import com.alroca.apr_xativa.entity.Documento;
import com.alroca.apr_xativa.repository.DocumentoRepository;
import com.alroca.apr_xativa.service.UsuarioService;
import com.alroca.apr_xativa.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        String ruta = "uploads/" + usuarioId + "_" + archivo.getOriginalFilename();
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
}