package com.alroca.apr_xativa.repository;

import com.alroca.apr_xativa.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    List<Vehiculo> findByUsuarioIdAndActivoTrue(Long usuarioId);
    Optional<Vehiculo> findByMatriculaAndUsuarioId(String matricula, Long usuarioId);
    boolean existsByMatriculaAndUsuarioId(String matricula, Long usuarioId);
    List<Vehiculo> findByUsuarioId(Long usuarioId);
}