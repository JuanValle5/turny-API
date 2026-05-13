package com.turny.ApiTurny.domain.service;


import com.turny.ApiTurny.domain.dto.auth.AuthResponse;
import com.turny.ApiTurny.domain.dto.auth.LoginRequest;
import com.turny.ApiTurny.domain.dto.auth.RegisterRequest;
import com.turny.ApiTurny.domain.entity.Business;
import com.turny.ApiTurny.domain.entity.Client;
import com.turny.ApiTurny.domain.entity.User;
import com.turny.ApiTurny.domain.repository.UserRepository;
import com.turny.ApiTurny.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("El email ya está registrado");
        }

        // Validación extra para business
        if ("business".equals(request.tipo())) {
            if (request.nombreNegocio() == null || request.nombreNegocio().isBlank()) {
                throw new IllegalArgumentException("El nombre del negocio es obligatorio");
            }
            if (request.categoria() == null || request.categoria().isBlank()) {
                throw new IllegalArgumentException("La categoría del negocio es obligatoria");
            }
            if (request.direccion() == null || request.direccion().isBlank()) {
                throw new IllegalArgumentException("La dirección del negocio es obligatoria");
            }
        }

        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .nombre(request.nombre())
                .telefono(request.telefono())
                .tipo(request.tipo())
                .authProvider("email")
                .build();

        // Crea el perfil según el tipo
        if ("client".equals(request.tipo())) {
            Client client = Client.builder().user(user).build();
            user.setClient(client);

        } else {
            // El Business se completa en un paso posterior (onboarding)
            Business business = Business.builder()
                    .user(user)
                    .nombre(request.nombreNegocio())
                    .categoria(request.categoria())
                    .direccion(request.direccion())
                    .codigo(generarCodigo())
                    .telefono(user.getTelefono() != null ? user.getTelefono() : "")
                    .build();
            user.setBusiness(business);
        }

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getEmail(), user.getTipo(), user.getId().toString()
        );

        return new AuthResponse(
                token, user.getId(), user.getTipo(), user.getNombre(), user.getEmail()
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        String token = jwtUtil.generateToken(
                user.getEmail(), user.getTipo(), user.getId().toString()
        );

        return new AuthResponse(token, user.getId(), user.getTipo(), user.getNombre(), user.getEmail());
    }

    // Genera un código único de 8 caracteres para el negocio (ej: TRN-A3F9)
    private String generarCodigo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder("TRN-");
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
