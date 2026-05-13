package com.turny.ApiTurny.domain.dto.appointment;

import jakarta.validation.constraints.*;

public record CambiarEstadoRequest(
        @NotBlank @Pattern(regexp = "confirmada|completada|cancelada|no_asistio")
        String estado,

        String motivo    // obligatorio solo si estado = "cancelada"
) {}