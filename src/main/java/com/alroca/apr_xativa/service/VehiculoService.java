package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Vehiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehiculoService {
    List<Vehiculo> findByUsuario(Long usuarioId);
    List<Vehiculo> findAllByUsuario(Long usuarioId);
    List<Vehiculo> findAll();
    Page<Vehiculo> findAllPaginado(Pageable pageable);
    Vehiculo alta(Long usuarioId, String matricula, Vehiculo.TipoAcred tipoAcred);
    void baja(Long vehiculoId, Long usuarioId);
    void reactivar(Long vehiculoId, Long usuarioId);
}