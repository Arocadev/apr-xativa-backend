package com.alroca.apr_xativa.simulador;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/simulador")
@RequiredArgsConstructor
public class SimuladorController {

    private final SimuladorService simuladorService;

    @GetMapping("/comprobar")
    public ResponseEntity<Map<String, Object>> comprobar() {
        return ResponseEntity.ok(simuladorService.comprobarMatricula());
    }
}