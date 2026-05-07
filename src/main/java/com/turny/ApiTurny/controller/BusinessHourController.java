package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.business.BusinessHourRequest;
import com.turny.ApiTurny.domain.dto.business.BusinessHourResponse;
import com.turny.ApiTurny.domain.service.BusinessHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business-hours")
@RequiredArgsConstructor
public class BusinessHourController {

    private final BusinessHourService businessHourService;

    // GET público — cualquier cliente puede ver los horarios de un negocio
    @GetMapping("/{negocioId}")
    public ResponseEntity<List<BusinessHourResponse>> getHorarios(
            @PathVariable UUID negocioId
    ) {
        return ResponseEntity.ok(businessHourService.getHorarios(negocioId));
    }

    // PUT protegido — solo el dueño del negocio puede modificar sus horarios
    @PutMapping
    public ResponseEntity<List<BusinessHourResponse>> guardarHorarios(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody List<BusinessHourRequest> request
    ) {
        return ResponseEntity.ok(
                businessHourService.guardarHorarios(userDetails.getUsername(), request)
        );
    }
}

