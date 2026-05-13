package com.turny.ApiTurny.domain.dto.review;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID negocioId,
        String clienteNombre,
        String clienteAvatar,
        Short calificacion,
        String estrellas,        // "★★★★☆" — útil para mostrar directo en la app
        String comentario,
        String respuesta,
        Instant fechaRespuesta,
        Instant createdAt
) {}
