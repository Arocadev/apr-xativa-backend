package com.alroca.apr_xativa.mapper;

import com.alroca.apr_xativa.dto.UsuarioRequestDTO;
import com.alroca.apr_xativa.dto.UsuarioResponseDTO;
import com.alroca.apr_xativa.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponse(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setDni(usuario.getDni());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().name());
        dto.setTipo(usuario.getTipo());
        dto.setNumCamas(usuario.getNumCamas());
        dto.setNumPlazas(usuario.getNumPlazas());
        dto.setNumTrabajadores(usuario.getNumTrabajadores());
        dto.setActivo(usuario.isActivo());
        dto.setCreatedAt(usuario.getCreatedAt());
        return dto;
    }

    public Usuario toEntity(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setDni(dto.getDni());
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setTipo(dto.getTipo());
        usuario.setNumCamas(dto.getNumCamas());
        usuario.setNumPlazas(dto.getNumPlazas());
        usuario.setNumTrabajadores(dto.getNumTrabajadores());
        return usuario;
    }
}