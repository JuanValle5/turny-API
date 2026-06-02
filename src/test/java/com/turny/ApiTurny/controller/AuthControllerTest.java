package com.turny.ApiTurny.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turny.ApiTurny.domain.dto.auth.AuthResponse;
import com.turny.ApiTurny.domain.dto.auth.LoginRequest;
import com.turny.ApiTurny.domain.dto.auth.RegisterRequest;
import com.turny.ApiTurny.domain.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private UUID testProfileId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testProfileId = UUID.randomUUID();
    }

    @Test
    @DisplayName("GET /auth should return prueba message")
    void testGetPrueba() throws Exception {
        mockMvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(content().string("Prueba"));
    }

    @Test
    @DisplayName("POST /auth/register - Successful client registration")
    void testRegisterClientSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "client@test.com",
                "password123",
                "Juan",
                "1234567890",
                "client",
                null,
                null,
                null
        );

        AuthResponse response = new AuthResponse(
                "token123",
                testUserId,
                testProfileId,
                "client",
                "Juan",
                "client@test.com"
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("client@test.com")))
                .andExpect(jsonPath("$.tipo", is("client")))
                .andExpect(jsonPath("$.token", is("token123")));
    }

    @Test
    @DisplayName("POST /auth/register - Successful business registration")
    void testRegisterBusinessSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "negocio@test.com",
                "password123",
                "Admin",
                "1234567890",
                "business",
                "Mi Negocio",
                "Salon de Belleza",
                "Calle Principal 123"
        );

        AuthResponse response = new AuthResponse(
                "token456",
                testUserId,
                testProfileId,
                "business",
                "Admin",
                "negocio@test.com"
        );

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("negocio@test.com")))
                .andExpect(jsonPath("$.tipo", is("business")));
    }

    @Test
    @DisplayName("POST /auth/register - Duplicate email should fail")
    void testRegisterDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "duplicate@test.com",
                "password123",
                "Juan",
                "1234567890",
                "client",
                null,
                null,
                null
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalStateException("El email ya está registrado"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /auth/register - Missing required fields should fail")
    void testRegisterMissingFields() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "",
                "password123",
                "Juan",
                "1234567890",
                "client",
                null,
                null,
                null
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /auth/login - Successful login")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("client@test.com", "password123");

        AuthResponse response = new AuthResponse(
                "token789",
                testUserId,
                testProfileId,
                "client",
                "Juan",
                "client@test.com"
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("client@test.com")))
                .andExpect(jsonPath("$.token", is("token789")));
    }

    @Test
    @DisplayName("POST /auth/login - Invalid credentials should fail")
    void testLoginInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("invalid@test.com", "wrongpass");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /auth/login - Missing email")
    void testLoginMissingEmail() throws Exception {
        LoginRequest request = new LoginRequest("", "password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("POST /auth/login - Missing password")
    void testLoginMissingPassword() throws Exception {
        LoginRequest request = new LoginRequest("client@test.com", "");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
