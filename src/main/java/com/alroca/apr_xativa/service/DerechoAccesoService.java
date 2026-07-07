package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DerechoAccesoService {
    List<DerechoAcceso> findByUsuario(Long usuarioId);
    Page<DerechoAcceso> findByUsuarioPaginado(Long usuarioId, Pageable pageable);
    DerechoAcceso crearPermanente(Long usuarioId, Long vehiculoId);
    DerechoAcceso crearPuntual(Long usuarioId, Long vehiculoId, LocalDate fecha);
    DerechoAcceso crearPuntualInvitado(Long usuarioId, String matricula, LocalDate fecha);
    void eliminar(Long derechoId, Long usuarioId);
}