package com.alroca.apr_xativa.mapper;

import com.alroca.apr_xativa.dto.VehiculoRequestDTO;
import com.alroca.apr_xativa.dto.VehiculoResponseDTO;
import com.alroca.apr_xativa.entity.Vehiculo;
import org.springframework.stereotype.Component;

@Component
public class VehiculoMapper {

    public VehiculoResponseDTO toResponse(Vehiculo vehiculo) {
        VehiculoResponseDTO dto = new VehiculoResponseDTO();
        dto.setId(vehiculo.getId());
        dto.setMatricula(vehiculo.getMatricula());
        dto.setTipoAcred(vehiculo.getTipoAcred().name());
        dto.setActivo(vehiculo.isActivo());
        dto.setCreatedAt(vehiculo.getCreatedAt());
        dto.setUsuarioId(vehiculo.getUsuario().getId());
        return dto;
    }

    public Vehiculo toEntity(VehiculoRequestDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setMatricula(dto.getMatricula().toUpperCase());
        vehiculo.setTipoAcred(Vehiculo.TipoAcred.valueOf(dto.getTipoAcred()));
        return vehiculo;
    }
}
