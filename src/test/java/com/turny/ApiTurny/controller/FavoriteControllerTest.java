package com.turny.ApiTurny.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turny.ApiTurny.domain.dto.favorite.FavoriteResponse;
import com.turny.ApiTurny.domain.dto.favorite.ToggleFavoriteResponse;
import com.turny.ApiTurny.domain.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("FavoriteController Tests")
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID negocioId;
    private UUID favoriteId;
    private FavoriteResponse favoriteResponse;

    @BeforeEach
    void setUp() {
        negocioId = UUID.randomUUID();
        favoriteId = UUID.randomUUID();

        favoriteResponse = new FavoriteResponse(
                favoriteId,
                negocioId,
                "Salon de Belleza",
                "Belleza",
                "Calle Principal 123",
                "https://example.com/image.png",
                "https://example.com/logo.png",
                4.5,
                25,
                Instant.now()
        );
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("GET /api/favorites - Should return my favorites")
    void testGetMisFavoritos() throws Exception {
        List<FavoriteResponse> favorites = Arrays.asList(favoriteResponse);
        when(favoriteService.getMisFavoritos("cliente@test.com")).thenReturn(favorites);

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombreNegocio", is("Salon de Belleza")))
                .andExpect(jsonPath("$[0].rating", is(4.5)));
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("GET /api/favorites - Should return empty list when no favorites")
    void testGetMisFavoritosEmpty() throws Exception {
        when(favoriteService.getMisFavoritos("cliente@test.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("POST /api/favorites/{negocioId}/toggle - Should add favorite")
    void testToggleFavoriteAdd() throws Exception {
        ToggleFavoriteResponse response = new ToggleFavoriteResponse(
                negocioId,
                true,
                "Negocio agregado a favoritos"
        );
        when(favoriteService.toggle("cliente@test.com", negocioId)).thenReturn(response);

        mockMvc.perform(post("/api/favorites/{negocioId}/toggle", negocioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esFavorito", is(true)))
                .andExpect(jsonPath("$.mensaje", containsString("agregado")));
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("POST /api/favorites/{negocioId}/toggle - Should remove favorite")
    void testToggleFavoriteRemove() throws Exception {
        ToggleFavoriteResponse response = new ToggleFavoriteResponse(
                negocioId,
                false,
                "Negocio eliminado de favoritos"
        );
        when(favoriteService.toggle("cliente@test.com", negocioId)).thenReturn(response);

        mockMvc.perform(post("/api/favorites/{negocioId}/toggle", negocioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esFavorito", is(false)))
                .andExpect(jsonPath("$.mensaje", containsString("eliminado")));
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("GET /api/favorites/{negocioId}/check - Should return true when favorite")
    void testCheckIsFavorite() throws Exception {
        ToggleFavoriteResponse response = new ToggleFavoriteResponse(
                negocioId,
                true,
                "Es favorito"
        );
        when(favoriteService.esFavorito("cliente@test.com", negocioId)).thenReturn(true);

        mockMvc.perform(get("/api/favorites/{negocioId}/check", negocioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esFavorito", is(true)));
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("GET /api/favorites/{negocioId}/check - Should return false when not favorite")
    void testCheckIsNotFavorite() throws Exception {
        when(favoriteService.esFavorito("cliente@test.com", negocioId)).thenReturn(false);

        mockMvc.perform(get("/api/favorites/{negocioId}/check", negocioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esFavorito", is(false)));
    }

    @Test
    @DisplayName("GET /api/favorites - Should fail without authentication")
    void testGetFavoritesUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("POST /api/favorites/{negocioId}/toggle - Should fail with invalid UUID")
    void testToggleFavoriteInvalidUUID() throws Exception {
        mockMvc.perform(post("/api/favorites/invalid-uuid/toggle"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("GET /api/favorites - Should return correct favorite structure")
    void testGetFavoritesCorrectStructure() throws Exception {
        List<FavoriteResponse> favorites = Arrays.asList(favoriteResponse);
        when(favoriteService.getMisFavoritos("cliente@test.com")).thenReturn(favorites);

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isMap())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].negocioId").exists())
                .andExpect(jsonPath("$[0].nombreNegocio").exists())
                .andExpect(jsonPath("$[0].categoria").exists())
                .andExpect(jsonPath("$[0].direccion").exists());
    }

    @Test
    @DisplayName("POST /api/favorites/{negocioId}/toggle - Should fail without authentication")
    void testToggleFavoriteUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/favorites/{negocioId}/toggle", negocioId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/favorites/{negocioId}/check - Should fail without authentication")
    void testCheckFavoriteUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/favorites/{negocioId}/check", negocioId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cliente@test.com")
    @DisplayName("GET /api/favorites - Should return favorites ordered by creation date")
    void testGetFavoritesOrderedByDate() throws Exception {
        FavoriteResponse fav1 = new FavoriteResponse(
                UUID.randomUUID(), negocioId, "Negocio 1", "Categoria", "Dir", "url", "url", 4.5, 10, Instant.now().minusSeconds(3600)
        );
        FavoriteResponse fav2 = new FavoriteResponse(
                UUID.randomUUID(), UUID.randomUUID(), "Negocio 2", "Categoria", "Dir", "url", "url", 4.8, 20, Instant.now()
        );
        List<FavoriteResponse> favorites = Arrays.asList(fav2, fav1);
        when(favoriteService.getMisFavoritos("cliente@test.com")).thenReturn(favorites);

        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombreNegocio", is("Negocio 2")))
                .andExpect(jsonPath("$[1].nombreNegocio", is("Negocio 1")));
    }
}
