package com.alroca.apr_xativa.simulador;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import com.alroca.apr_xativa.repository.DerechoAccesoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimuladorServiceImpl implements SimuladorService {

    private final DerechoAccesoRepository derechoAccesoRepository;

    private static final String LETRAS_VALIDAS = "BCDFGHJKLMNPRSTUVWXYZ";
    private static final Random random = new Random();

    @Override
    public Map<String, Object> comprobarMatricula() {
        boolean usarMatriculaReal = random.nextInt(100) < 60;

        if (usarMatriculaReal) {
            return comprobarMatriculaReal();
        } else {
            return comprobarMatriculaAleatoria();
        }
    }

    private Map<String, Object> comprobarMatriculaReal() {
        LocalDate hoy = LocalDate.now();
        List<DerechoAcceso> derechos = derechoAccesoRepository.findDerechosActivosHoy(hoy);

        if (derechos.isEmpty()) {
            return comprobarMatriculaAleatoria();
        }

        DerechoAcceso derecho = derechos.get(random.nextInt(derechos.size()));
        String matricula = derecho.getVehiculo() != null
                ? derecho.getVehiculo().getMatricula()
                : derecho.getMatriculaInvitado();

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("acceso", true);
        resultado.put("matricula", matricula);
        resultado.put("tipoDerecho", derecho.getTipoDerecho().name());
        resultado.put("tipoAcred", derecho.getTipoAcred().name());
        resultado.put("fechaFin", derecho.getFechaFin().toString());
        resultado.put("motivo", derecho.getTipoDerecho() == DerechoAcceso.TipoDerecho.PERMANENTE
                ? "Dret permanent actiu"
                : "Dret puntual actiu per a hui");
        return resultado;
    }

    private Map<String, Object> comprobarMatriculaAleatoria() {
        String matricula = generarMatriculaAleatoria();

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("acceso", false);
        resultado.put("matricula", matricula);
        resultado.put("tipoDerecho", null);
        resultado.put("tipoAcred", null);
        resultado.put("fechaFin", null);
        resultado.put("motivo", "Sense dret d'accés actiu per a hui");
        return resultado;
    }

    private String generarMatriculaAleatoria() {
        String matricula;
        List<String> matriculasExistentes = derechoAccesoRepository
                .findDerechosActivosHoy(LocalDate.now())
                .stream()
                .map(d -> d.getVehiculo() != null
                        ? d.getVehiculo().getMatricula()
                        : d.getMatriculaInvitado())
                .filter(Objects::nonNull)
                .toList();

        do {
            int numeros = 1000 + random.nextInt(9000);
            StringBuilder letras = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                letras.append(LETRAS_VALIDAS.charAt(random.nextInt(LETRAS_VALIDAS.length())));
            }
            matricula = numeros + letras.toString();
        } while (matriculasExistentes.contains(matricula));

        return matricula;
    }
}