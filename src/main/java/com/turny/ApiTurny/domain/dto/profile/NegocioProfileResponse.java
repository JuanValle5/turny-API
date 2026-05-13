package com.turny.ApiTurny.domain.dto.profile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record NegocioProfileResponse(
        UUID userId,
        UUID negocioId,
        String nombre,           // nombre del dueño
        String email,
        String telefono,
        String avatarUrl,
        String nombreNegocio,
        String categoria,
        String descripcion,
        String direccion,
        String ciudad,
        String codigoPostal,
        BigDecimal latitud,
        BigDecimal longitud,
        String whatsapp,
        String emailNegocio,
        String website,
        String imagenUrl,
        String logoUrl,
        String codigo,           // código único del negocio — solo lectura
        BigDecimal rating,
        Integer totalResenas,
        Boolean verificado,
        Instant createdAt
) {}
