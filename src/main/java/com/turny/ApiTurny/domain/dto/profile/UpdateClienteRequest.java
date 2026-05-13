package com.turny.ApiTurny.domain.dto.profile;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UpdateClienteRequest(
        @NotBlank @Size(max = 100)
        String nombre,

        @Size(max = 20)
        String telefono,

        String avatarUrl,
        LocalDate fechaNacimiento,
        String genero,
        String direccion,
        String notas
) {}
