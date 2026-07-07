package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DerechoAccesoRepository extends JpaRepository<DerechoAcceso, Long> {

    List<DerechoAcceso> findByUsuarioIdAndActivoTrue(Long usuarioId);

    Page<DerechoAcceso> findByUsuarioIdAndActivoTrue(Long usuarioId, Pageable pageable);

    List<DerechoAcceso> findByVehiculoIdAndActivoTrue(Long vehiculoId);

    @Query("SELECT COUNT(d) FROM DerechoAcceso d WHERE d.usuario.id = :usuarioId " +
            "AND d.tipoDerecho = 'PUNTUAL' " +
            "AND d.matriculaInvitado IS NOT NULL " +
            "AND YEAR(d.fechaInicio) = :anyo " +
            "AND MONTH(d.fechaInicio) = :mes " +
            "AND d.activo = true")
    long countInvitacionesMes(
            @Param("usuarioId") Long usuarioId,
            @Param("anyo") int anyo,
            @Param("mes") int mes
    );

    @Query("SELECT d FROM DerechoAcceso d WHERE d.activo = true " +
            "AND d.fechaInicio <= :hoy AND d.fechaFin >= :hoy")
    List<DerechoAcceso> findDerechosActivosHoy(@Param("hoy") LocalDate hoy);

    @Modifying
    @Query("UPDATE DerechoAcceso d SET d.activo = false WHERE d.fechaFin < :hoy AND d.activo = true")
    int desactivarExpirados(@Param("hoy") LocalDate hoy);
}