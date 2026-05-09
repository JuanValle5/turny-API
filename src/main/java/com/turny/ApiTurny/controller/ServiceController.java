package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.service.ServiceRequest;
import com.turny.ApiTurny.domain.dto.service.ServiceResponse;
import com.turny.ApiTurny.domain.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    // Público — cualquier cliente puede ver los servicios de un negocio
    @GetMapping("/{negocioId}")
    public ResponseEntity<List<ServiceResponse>> getServicios(
            @PathVariable UUID negocioId
    ) {
        return ResponseEntity.ok(serviceService.getServicios(negocioId));
    }

    // Protegido — solo el negocio autenticado
    @PostMapping
    public ResponseEntity<ServiceResponse> crear(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ServiceRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(serviceService.crear(userDetails.getUsername(), request));
    }

    @PutMapping("/{servicioId}")
    public ResponseEntity<ServiceResponse> editar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID servicioId,
            @Valid @RequestBody ServiceRequest request
    ) {
        return ResponseEntity.ok(
                serviceService.editar(userDetails.getUsername(), servicioId, request)
        );
    }

    @DeleteMapping("/{servicioId}")
    public ResponseEntity<Void> desactivar(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID servicioId
    ) {
        serviceService.desactivar(userDetails.getUsername(), servicioId);
        return ResponseEntity.noContent().build();
    }
}
