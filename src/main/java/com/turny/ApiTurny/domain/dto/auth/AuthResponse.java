package com.turny.ApiTurny.domain.dto.auth;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        UUID perfilId,
        String tipo,
        String nombre,
        String email
) {}