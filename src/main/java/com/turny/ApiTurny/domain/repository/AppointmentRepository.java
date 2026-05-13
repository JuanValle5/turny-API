package com.turny.ApiTurny.domain.repository;

import com.turny.ApiTurny.domain.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    // Citas del cliente ordenadas por fecha
    List<Appointment> findByClienteIdOrderByFechaDescHoraDesc(UUID clienteId);

    // Citas del negocio en una fecha
    List<Appointment> findByNegocioIdAndFechaOrderByHoraAsc(UUID negocioId, LocalDate fecha);

    // Citas activas del negocio en una fecha (para validar disponibilidad)
    // Excluye canceladas y no_asistio
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.negocio.id = :negocioId
        AND a.fecha = :fecha
        AND a.estado NOT IN ('cancelada', 'no_asistio')
    """)
    List<Appointment> findCitasActivasPorFecha(
            @Param("negocioId") UUID negocioId,
            @Param("fecha") LocalDate fecha
    );

    // Verifica si un slot específico ya está ocupado
    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.negocio.id = :negocioId
        AND a.fecha = :fecha
        AND a.hora = :hora
        AND a.estado NOT IN ('cancelada', 'no_asistio')
    """)
    boolean existeConflicto(
            @Param("negocioId") UUID negocioId,
            @Param("fecha") LocalDate fecha,
            @Param("hora") LocalTime hora
    );

    // Citas próximas para recordatorios (scheduled job futuro)
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.fecha = :fecha
        AND a.recordatorioEnviado = false
        AND a.estado = 'confirmada'
    """)
    List<Appointment> findCitasParaRecordatorio(@Param("fecha") LocalDate fecha);
}
