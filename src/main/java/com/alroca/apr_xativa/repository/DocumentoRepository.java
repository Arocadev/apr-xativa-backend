package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByUsuarioId(Long usuarioId);
}