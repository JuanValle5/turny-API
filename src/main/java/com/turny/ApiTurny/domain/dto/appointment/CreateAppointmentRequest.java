package com.turny.ApiTurny.domain.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull
        UUID negocioId,

        @NotNull
        UUID servicioId,

        @NotNull @Future
        LocalDate fecha,

        @NotNull
        LocalTime hora,

        String notasCliente
) {}
