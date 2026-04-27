package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DerechoAccesoRepository extends JpaRepository<DerechoAcceso, Long> {

    List<DerechoAcceso> findByUsuarioIdAndActivoTrue(Long usuarioId);

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
}