package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.DerechoAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DerechoAccesoRepository extends JpaRepository<DerechoAcceso, Long> {
    List<DerechoAcceso> findByUsuarioIdAndActivoTrue(Long usuarioId);
    List<DerechoAcceso> findByVehiculoIdAndActivoTrue(Long vehiculoId);
    long countByUsuarioIdAndTipoDerechoAndTipoAcredAndActivoTrue(
            Long usuarioId,
            DerechoAcceso.TipoDerecho tipoDerecho,
            com.alroca.apr_xativa.entity.Vehiculo.TipoAcred tipoAcred
    );
    long countByUsuarioIdAndTipoDerechoAndFechaInicioBeforeAndFechaFinAfterAndActivoTrue(
            Long usuarioId,
            DerechoAcceso.TipoDerecho tipoDerecho,
            LocalDate fechaFin,
            LocalDate fechaInicio
    );
}
