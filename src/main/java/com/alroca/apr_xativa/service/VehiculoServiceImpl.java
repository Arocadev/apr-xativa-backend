package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.entity.Vehiculo;
import com.alroca.apr_xativa.exception.AccesoNoPermitidoException;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.VehiculoNotFoundException;
import com.alroca.apr_xativa.repository.VehiculoRepository;
import com.alroca.apr_xativa.utils.ValidacionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioService usuarioService;

    @Override
    public List<Vehiculo> findByUsuario(Long usuarioId) {
        log.debug("Listando vehiculos del usuario id: {}", usuarioId);
        return vehiculoRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public List<Vehiculo> findAllByUsuario(Long usuarioId) {
        return vehiculoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> findAll() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Vehiculo alta(Long usuarioId, String matricula, Vehiculo.TipoAcred tipoAcred) {
        log.info("Intentando dar de alta matricula: {} para usuario id: {}", matricula, usuarioId);
        if (!ValidacionUtils.esMatriculaValida(matricula)) {
            log.warn("Matricula invalida: {}", matricula);
            throw new IllegalArgumentException("Formato de matricula incorrecto: " + matricula);
        }
        if (vehiculoRepository.existsByMatriculaAndUsuarioId(matricula, usuarioId)) {
            log.warn("Matricula duplicada: {} para usuario id: {}", matricula, usuarioId);
            throw new DuplicadoException("Ya tienes el vehiculo con matricula " + matricula + " registrado");
        }
        Usuario usuario = usuarioService.findById(usuarioId);
        Vehiculo vehiculo = Vehiculo.builder()
                .matricula(matricula.toUpperCase())
                .usuario(usuario)
                .tipoAcred(tipoAcred)
                .activo(true)
                .build();
        log.info("Vehiculo dado de alta correctamente: {} para usuario id: {}", matricula, usuarioId);
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public void baja(Long vehiculoId, Long usuarioId) {
        log.info("Intentando dar de baja vehiculo id: {} del usuario id: {}", vehiculoId, usuarioId);
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));
        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
            log.warn("Usuario id: {} intentó dar de baja vehiculo id: {} que no le pertenece", usuarioId, vehiculoId);
            throw new AccesoNoPermitidoException("No tienes permiso para dar de baja este vehiculo");
        }
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
        log.info("Vehiculo id: {} dado de baja correctamente", vehiculoId);
    }
}