package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.appointment.AppointmentResponse;
import com.turny.ApiTurny.domain.dto.appointment.CambiarEstadoRequest;
import com.turny.ApiTurny.domain.dto.appointment.CreateAppointmentRequest;
import com.turny.ApiTurny.domain.dto.appointment.SlotResponse;
import com.turny.ApiTurny.domain.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Slots disponibles — público
    @GetMapping("/slots")
    public ResponseEntity<List<SlotResponse>> getSlots(
            @RequestParam UUID negocioId,
            @RequestParam UUID servicioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        return ResponseEntity.ok(
                appointmentService.getSlotsDisponibles(negocioId, servicioId, fecha)
        );
    }

    // Crear cita — solo clientes
    @PostMapping
    public ResponseEntity<AppointmentResponse> crear(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateAppointmentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.crear(userDetails.getUsername(), request));
    }

    // Mis citas — cliente autenticado
    @GetMapping("/mis-citas")
    public ResponseEntity<List<AppointmentResponse>> getMisCitas(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                appointmentService.getMisCitas(userDetails.getUsername())
        );
    }

    // Citas del negocio — negocio autenticado
    @GetMapping("/negocio")
    public ResponseEntity<List<AppointmentResponse>> getCitasNegocio(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {
        return ResponseEntity.ok(
                appointmentService.getCitasNegocio(userDetails.getUsername(), fecha)
        );
    }

    // Cambiar estado — cliente o negocio
    @PatchMapping("/{citaId}/estado")
    public ResponseEntity<AppointmentResponse> cambiarEstado(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID citaId,
            @Valid @RequestBody CambiarEstadoRequest request
    ) {
        return ResponseEntity.ok(
                appointmentService.cambiarEstado(userDetails.getUsername(), citaId, request)
        );
    }
}
