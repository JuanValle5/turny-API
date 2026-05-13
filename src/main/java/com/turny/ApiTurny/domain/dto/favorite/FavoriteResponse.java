package com.turny.ApiTurny.domain.dto.favorite;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record FavoriteResponse(
        UUID favoriteId,
        UUID negocioId,
        String negocioNombre,
        String negocioCategoria,
        String negocioDireccion,
        String negocioImagen,
        String negocioLogo,
        BigDecimal rating,
        Integer totalResenas,
        Instant guardadoEn
) {}
