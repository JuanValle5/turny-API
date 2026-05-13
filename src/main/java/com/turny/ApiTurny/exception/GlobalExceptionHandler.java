package com.turny.ApiTurny.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 — Validación de @Valid ────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            campos.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(ApiError.ofValidation(campos));
    }

    // ── 400 — Lógica de negocio (IllegalArgumentException) ───────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Solicitud inválida", ex.getMessage())
        );
    }

    // ── 400 — Parámetro de tipo incorrecto en URL ─────────────────────────────
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        String mensaje = "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "Parámetro inválido", mensaje)
        );
    }

    // ── 401 — Credenciales incorrectas ────────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiError.of(401, "No autorizado", "Email o contraseña incorrectos")
        );
    }

    // ── 403 — Sin permisos ────────────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiError.of(403, "Acceso denegado", ex.getMessage())
        );
    }

    // ── 404 — Recurso no encontrado ───────────────────────────────────────────
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(
            NoSuchElementException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(404, "No encontrado", ex.getMessage())
        );
    }

    // ── 409 — Conflicto (email duplicado, código duplicado, etc.) ────────────
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(
            IllegalStateException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiError.of(409, "Conflicto", ex.getMessage())
        );
    }

    // ── 500 — Cualquier otra cosa ─────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex, HttpServletRequest request
    ) {
        // Log completo en consola para debugging
        System.err.println("Error no controlado en " + request.getRequestURI());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.of(500, "Error interno", "Ocurrió un error inesperado")
        );
    }
}
