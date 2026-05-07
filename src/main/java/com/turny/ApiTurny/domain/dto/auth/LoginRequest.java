package com.turny.ApiTurny.domain.dto.auth;

import jakarta.validation.constraints.*;

public record LoginRequest(
        @NotBlank @Email
        String email,

        @NotBlank
        String password
) {}
