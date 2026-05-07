package com.turny.ApiTurny.controller;

import com.turny.ApiTurny.domain.dto.auth.AuthResponse;
import com.turny.ApiTurny.domain.dto.auth.LoginRequest;
import com.turny.ApiTurny.domain.dto.auth.RegisterRequest;
import com.turny.ApiTurny.domain.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping
    public String prueba(){
        return "Prueba";
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
