package com.turny.ApiTurny.domain.dto.review;

import jakarta.validation.constraints.*;

public record ResponderReviewRequest(
        @NotBlank @Size(max = 1000)
        String respuesta
) {}
