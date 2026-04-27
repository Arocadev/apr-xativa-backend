package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import java.time.LocalDate;
import java.util.List;

public interface DerechoAccesoService {
    List<DerechoAcceso> findByUsuario(Long usuarioId);
    DerechoAcceso crearPermanente(Long usuarioId, Long vehiculoId);
    DerechoAcceso crearPuntual(Long usuarioId, Long vehiculoId, LocalDate fecha);
    DerechoAcceso crearPuntualInvitado(Long usuarioId, String matricula, LocalDate fecha);
    void eliminar(Long derechoId, Long usuarioId);
}