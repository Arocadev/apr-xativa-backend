package com.alroca.apr_xativa.mapper;

import com.alroca.apr_xativa.dto.DerechoAccesoResponseDTO;
import com.alroca.apr_xativa.entity.DerechoAcceso;
import org.springframework.stereotype.Component;

@Component
public class DerechoAccesoMapper {

    public DerechoAccesoResponseDTO toResponse(DerechoAcceso derecho) {
        DerechoAccesoResponseDTO dto = new DerechoAccesoResponseDTO();
        dto.setId(derecho.getId());
        dto.setUsuarioId(derecho.getUsuario().getId());
        dto.setVehiculoId(derecho.getVehiculo().getId());
        dto.setMatricula(derecho.getVehiculo().getMatricula());
        dto.setTipoDerecho(derecho.getTipoDerecho().name());
        dto.setTipoAcred(derecho.getTipoAcred().name());
        dto.setFechaInicio(derecho.getFechaInicio());
        dto.setFechaFin(derecho.getFechaFin());
        dto.setActivo(derecho.isActivo());
        dto.setCreatedAt(derecho.getCreatedAt());
        return dto;
    }
}