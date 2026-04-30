package com.alroca.apr_xativa.mapper;

import com.alroca.apr_xativa.dto.SolicitudResponseDTO;
import com.alroca.apr_xativa.entity.Solicitud;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public SolicitudResponseDTO toResponse(Solicitud solicitud) {
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setId(solicitud.getId());
        dto.setUsuarioId(solicitud.getUsuario().getId());
        dto.setUsuarioDni(solicitud.getUsuario().getDni());
        dto.setUsuarioNombre(solicitud.getUsuario().getNombre() + " " + solicitud.getUsuario().getApellidos());
        dto.setEstado(solicitud.getEstado().name());
        dto.setObservaciones(solicitud.getObservaciones());
        dto.setAdminId(solicitud.getAdmin() != null ? solicitud.getAdmin().getId() : null);
        dto.setCreatedAt(solicitud.getCreatedAt());
        dto.setGestionadaAt(solicitud.getGestionadaAt());
        return dto;
    }
}