package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.AuditoriaLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {
    List<AuditoriaLog> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    Page<AuditoriaLog> findAll(Pageable pageable);
}