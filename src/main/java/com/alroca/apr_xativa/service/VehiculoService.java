package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Vehiculo;
import java.util.List;

public interface VehiculoService {
    List<Vehiculo> findByUsuario(Long usuarioId);
    Vehiculo alta(Long usuarioId, String matricula, Vehiculo.TipoAcred tipoAcred);
    void baja(Long vehiculoId, Long usuarioId);
    List<Vehiculo> findAllByUsuario(Long usuarioId);
    List<Vehiculo> findAll();
}
