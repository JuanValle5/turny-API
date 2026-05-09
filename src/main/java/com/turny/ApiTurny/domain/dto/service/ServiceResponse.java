package com.turny.ApiTurny.domain.dto.service;

import java.math.BigDecimal;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        String nombre,
        String descripcion,
        Short duracion,
        String duracionFormateada, // "45 min" | "1h 30min"
        BigDecimal precio,
        Boolean precioDesde,
        String categoria,
        String imagenUrl,
        Short orden,
        Boolean activo
) {}

