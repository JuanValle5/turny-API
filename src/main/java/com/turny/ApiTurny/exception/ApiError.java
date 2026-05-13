package com.turny.ApiTurny.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) // omite campos null en el JSON
public record ApiError(
        int status,
        String error,
        String mensaje,
        Instant timestamp,
        Map<String, String> campos   // solo se usa en errores de validación
) {
    // Constructor sin campos (la mayoría de errores)
    public static ApiError of(int status, String error, String mensaje) {
        return new ApiError(status, error, mensaje, Instant.now(), null);
    }

    // Constructor con campos (errores de @Valid)
    public static ApiError ofValidation(Map<String, String> campos) {
        return new ApiError(400, "Validación fallida", "Revisa los campos", Instant.now(), campos);
    }
}
