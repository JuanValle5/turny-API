package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.review.*;
import com.turny.ApiTurny.domain.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Público — reseñas de un negocio
    @GetMapping("/negocio/{negocioId}")
    public ResponseEntity<List<ReviewResponse>> getResenasNegocio(
            @PathVariable UUID negocioId
    ) {
        return ResponseEntity.ok(reviewService.getResenasNegocio(negocioId));
    }

    // Público — resumen del rating
    @GetMapping("/negocio/{negocioId}/resumen")
    public ResponseEntity<RatingResumen> getRatingResumen(
            @PathVariable UUID negocioId
    ) {
        return ResponseEntity.ok(reviewService.getRatingResumen(negocioId));
    }

    // Cliente — crear reseña
    @PostMapping
    public ResponseEntity<ReviewResponse> crear(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.crear(userDetails.getUsername(), request));
    }

    // Cliente — mis reseñas
    @GetMapping("/mis-resenas")
    public ResponseEntity<List<ReviewResponse>> getMisResenas(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                reviewService.getMisResenas(userDetails.getUsername())
        );
    }

    // Negocio — responder reseña
    @PatchMapping("/{reviewId}/responder")
    public ResponseEntity<ReviewResponse> responder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID reviewId,
            @Valid @RequestBody ResponderReviewRequest request
    ) {
        return ResponseEntity.ok(
                reviewService.responder(userDetails.getUsername(), reviewId, request)
        );
    }
}