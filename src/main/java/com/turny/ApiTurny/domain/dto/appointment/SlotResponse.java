package com.turny.ApiTurny.domain.dto.appointment;

import java.time.LocalTime;

public record SlotResponse(
        LocalTime hora,
        String horaFormateada,   // "09:00 AM"
        boolean disponible
) {}
