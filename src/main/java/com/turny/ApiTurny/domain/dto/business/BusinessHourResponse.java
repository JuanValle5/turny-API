package com.turny.ApiTurny.domain.dto.business;
import java.util.UUID;

public record BusinessHourResponse(
        UUID id,
        Short diaSemana,
        String diaNombre,      // "Lunes", "Martes"... para mostrar en la app
        Boolean abierto,
        String horaApertura,
        String horaCierre,
        String descansoInicio,
        String descansoFin
) {}
