package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Usuario;
import com.alroca.apr_xativa.entity.Vehiculo;
import com.alroca.apr_xativa.exception.AccesoNoPermitidoException;
import com.alroca.apr_xativa.exception.DuplicadoException;
import com.alroca.apr_xativa.exception.VehiculoNotFoundException;
import com.alroca.apr_xativa.repository.VehiculoRepository;
import com.alroca.apr_xativa.utils.ValidacionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final UsuarioService usuarioService;

    @Override
    public List<Vehiculo> findByUsuario(Long usuarioId) {
        return vehiculoRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    @Override
    public Vehiculo alta(Long usuarioId, String matricula, Vehiculo.TipoAcred tipoAcred) {
        if (!ValidacionUtils.esMatriculaValida(matricula)) {
            throw new IllegalArgumentException("Formato de matricula incorrecto: " + matricula);
        }
        if (vehiculoRepository.existsByMatriculaAndUsuarioId(matricula, usuarioId)) {
            throw new DuplicadoException("Ya tienes el vehiculo con matricula " + matricula + " registrado");
        }
        Usuario usuario = usuarioService.findById(usuarioId);
        Vehiculo vehiculo = Vehiculo.builder()
                .matricula(matricula.toUpperCase())
                .usuario(usuario)
                .tipoAcred(tipoAcred)
                .activo(true)
                .build();
        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public void baja(Long vehiculoId, Long usuarioId) {
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));
        if (!vehiculo.getUsuario().getId().equals(usuarioId)) {
            throw new AccesoNoPermitidoException("No tienes permiso para dar de baja este vehiculo");
        }
        vehiculo.setActivo(false);
        vehiculoRepository.save(vehiculo);
    }
}