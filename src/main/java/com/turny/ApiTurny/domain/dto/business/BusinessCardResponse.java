package com.turny.ApiTurny.domain.dto.business;


import java.math.BigDecimal;
import java.util.UUID;

public record BusinessCardResponse(
        UUID negocioId,
        String nombre,
        String categoria,
        String direccion,
        String logoUrl,
        BigDecimal rating,
        Integer totalResenas,
        String horarioHoy       // "9:00 - 18:00" | "Cerrado hoy"
) {}
