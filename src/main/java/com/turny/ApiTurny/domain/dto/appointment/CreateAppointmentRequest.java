package com.turny.ApiTurny.domain.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CreateAppointmentRequest(
        @NotNull
        UUID negocioId,

        @NotNull
        UUID servicioId,

        @NotNull @FutureOrPresent
        LocalDate fecha,

        @NotNull
        LocalTime hora,

        String notasCliente
) {}
