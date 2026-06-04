package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.business.BusinessCardResponse;
import com.turny.ApiTurny.domain.dto.profile.NegocioProfileResponse;
import com.turny.ApiTurny.domain.service.BusinessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    // Público — cualquier cliente puede ver los negocios
    @GetMapping
    public ResponseEntity<List<BusinessCardResponse>> getCards() {
        return ResponseEntity.ok(businessService.getCards());
    }

    @GetMapping("/code/{codigo}")
    public ResponseEntity<BusinessCardResponse> getByCode(
            @PathVariable String codigo
    ) {
        return ResponseEntity.ok(businessService.getByCode(codigo));
    }

    @GetMapping("/{negocioId}")
    public ResponseEntity<NegocioProfileResponse> getPerfilPublico(
            @PathVariable UUID negocioId
    ) {
        return ResponseEntity.ok(businessService.getPerfilPublico(negocioId));
    }
}
