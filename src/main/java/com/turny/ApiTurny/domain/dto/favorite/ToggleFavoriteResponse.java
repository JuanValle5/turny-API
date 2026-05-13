package com.turny.ApiTurny.domain.dto.favorite;

import java.util.UUID;

public record ToggleFavoriteResponse(
        UUID negocioId,
        boolean esFavorito,     // true = se agregó, false = se eliminó
        String mensaje
) {}
