package com.turny.ApiTurny.domain.dto.service;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ServiceRequest(
        @NotBlank @Size(max = 100)
        String nombre,

        String descripcion,

        @NotNull @Min(5) @Max(480)
        Short duracion,         // minutos — mín 5, máx 8 horas

        @NotNull @DecimalMin("0.0")
        BigDecimal precio,

        Boolean precioDesde,    // true = "Desde $X"

        @Size(max = 50)
        String categoria,

        String imagenUrl,

        Short orden             // posición en la lista
) {}
