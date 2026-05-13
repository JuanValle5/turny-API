package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.favorite.*;
import com.turny.ApiTurny.domain.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // Mis favoritos
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getMisFavoritos(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                favoriteService.getMisFavoritos(userDetails.getUsername())
        );
    }

    // Toggle — agrega o elimina en un solo endpoint
    @PostMapping("/{negocioId}/toggle")
    public ResponseEntity<ToggleFavoriteResponse> toggle(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID negocioId
    ) {
        return ResponseEntity.ok(
                favoriteService.toggle(userDetails.getUsername(), negocioId)
        );
    }

    // Verificar si un negocio es favorito — útil para pintar el ícono en la app
    @GetMapping("/{negocioId}/check")
    public ResponseEntity<ToggleFavoriteResponse> check(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID negocioId
    ) {
        boolean esFavorito = favoriteService.esFavorito(
                userDetails.getUsername(), negocioId
        );
        return ResponseEntity.ok(new ToggleFavoriteResponse(
                negocioId,
                esFavorito,
                esFavorito ? "Es favorito" : "No es favorito"
        ));
    }
}
