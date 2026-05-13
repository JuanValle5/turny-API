package com.turny.ApiTurny.domain.dto.appointment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID negocioId,
        String negocioNombre,
        String negocioImagen,
        UUID servicioId,
        String servicioNombre,
        Short duracion,
        String duracionFormateada,
        LocalDate fecha,
        LocalTime hora,
        LocalTime horaFin,          // hora + duracion — útil para mostrar en la app
        BigDecimal precio,
        String estado,
        String notasCliente,
        String notasNegocio
) {}
