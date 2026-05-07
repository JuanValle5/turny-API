package com.turny.ApiTurny.domain.dto.business;

import jakarta.validation.constraints.*;

public record BusinessHourRequest(
        @NotNull @Min(0) @Max(6)
        Short diaSemana,       // 0=Dom, 1=Lun, ..., 6=Sáb

        @NotNull
        Boolean abierto,

        // Solo obligatorios si abierto = true
        String horaApertura,   // "09:00"
        String horaCierre,     // "18:00"
        String descansoInicio, // "13:00" — opcional
        String descansoFin     // "14:00" — opcional
) {}
