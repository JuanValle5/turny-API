package com.turny.ApiTurny.domain.dto.profile;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ClienteProfileResponse(
        UUID userId,
        UUID clienteId,
        String nombre,
        String email,
        String telefono,
        String avatarUrl,
        LocalDate fechaNacimiento,
        String genero,
        String direccion,
        String notas,
        Integer totalCitas,
        BigDecimal totalGastado,
        Instant createdAt
) {}
