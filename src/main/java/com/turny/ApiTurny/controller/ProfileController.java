package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.profile.*;
import com.turny.ApiTurny.domain.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    // ── CLIENTE ───────────────────────────────────────────────────────────────

    @GetMapping("/cliente")
    public ResponseEntity<ClienteProfileResponse> getPerfilCliente(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                profileService.getPerfilCliente(userDetails.getUsername())
        );
    }

    @PutMapping("/cliente")
    public ResponseEntity<ClienteProfileResponse> updatePerfilCliente(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateClienteRequest request
    ) {
        return ResponseEntity.ok(
                profileService.updatePerfilCliente(userDetails.getUsername(), request)
        );
    }

    // ── NEGOCIO ───────────────────────────────────────────────────────────────

    @GetMapping("/negocio")
    public ResponseEntity<NegocioProfileResponse> getPerfilNegocio(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(
                profileService.getPerfilNegocio(userDetails.getUsername())
        );
    }

    @PutMapping("/negocio")
    public ResponseEntity<NegocioProfileResponse> updatePerfilNegocio(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateNegocioRequest request
    ) {
        return ResponseEntity.ok(
                profileService.updatePerfilNegocio(userDetails.getUsername(), request)
        );
    }

    // ── CAMBIAR CONTRASEÑA (ambos tipos) ──────────────────────────────────────

    @PatchMapping("/password")
    public ResponseEntity<Void> cambiarPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @NotBlank String passwordActual,
            @RequestParam @NotBlank @Size(min = 8) String passwordNuevo
    ) {
        profileService.cambiarPassword(
                userDetails.getUsername(),
                passwordActual,
                passwordNuevo,
                passwordEncoder
        );
        return ResponseEntity.noContent().build();
    }
}
