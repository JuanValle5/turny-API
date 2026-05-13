package com.turny.ApiTurny.domain.dto.review;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record CreateReviewRequest(
        @NotNull
        UUID citaId,          // la reseña siempre va ligada a una cita completada

        @NotNull @Min(1) @Max(5)
        Short calificacion,

        @Size(max = 1000)
        String comentario
) {}
