package com.alroca.apr_xativa.service;

import com.alroca.apr_xativa.entity.Solicitud;
import java.util.List;

public interface SolicitudService {
    Solicitud crear(Long usuarioId);
    List<Solicitud> findByUsuario(Long usuarioId);
    List<Solicitud> findPendientes();
    Solicitud aprobar(Long solicitudId, Long adminId);
    Solicitud rechazar(Long solicitudId, Long adminId, String observaciones);
    List<Solicitud> findAll();
}