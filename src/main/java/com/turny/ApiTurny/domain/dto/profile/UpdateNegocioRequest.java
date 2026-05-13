package com.turny.ApiTurny.domain.dto.profile;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateNegocioRequest(
        @NotBlank @Size(max = 100)
        String nombre,

        @NotBlank @Size(max = 100)
        String nombreNegocio,

        @Size(max = 20)
        String telefono,

        String avatarUrl,

        @NotBlank
        String categoria,

        String descripcion,

        @NotBlank
        String direccion,

        String ciudad,
        String codigoPostal,
        BigDecimal latitud,
        BigDecimal longitud,
        String whatsapp,

        @Email
        String emailNegocio,

        String website,
        String imagenUrl,
        String logoUrl
) {}
