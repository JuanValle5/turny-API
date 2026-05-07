package com.turny.ApiTurny.domain.dto.auth;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, message = "Mínimo 8 caracteres")
        String password,

        @NotBlank @Size(max = 100)
        String nombre,

        @Size(max = 20)
        String telefono,

        @NotBlank @Pattern(regexp = "business|client")
        String tipo,

        // Solo requeridos si tipo = "business"
        String nombreNegocio,
        String categoria,
        String direccion
) {}
