package com.turny.ApiTurny.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turny.ApiTurny.domain.dto.business.BusinessCardResponse;
import com.turny.ApiTurny.domain.service.BusinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BusinessController.class)
@DisplayName("BusinessController Tests")
class BusinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessService businessService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID businessId1;
    private UUID businessId2;
    private BusinessCardResponse business1;
    private BusinessCardResponse business2;

    @BeforeEach
    void setUp() {
        businessId1 = UUID.randomUUID();
        businessId2 = UUID.randomUUID();

        business1 = new BusinessCardResponse(
                businessId1,
                "Salon de Belleza",
                "Belleza",
                "Calle Principal 123",
                "https://example.com/logo1.png",
                4.5,
                25,
                "09:00 - 18:00"
        );

        business2 = new BusinessCardResponse(
                businessId2,
                "Barberia Premium",
                "Barberia",
                "Calle Secundaria 456",
                "https://example.com/logo2.png",
                4.8,
                42,
                "08:00 - 20:00"
        );
    }

    @Test
    @DisplayName("GET /api/businesses - Should return list of all businesses")
    void testGetBusinessesSuccess() throws Exception {
        List<BusinessCardResponse> businesses = Arrays.asList(business1, business2);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Salon de Belleza")))
                .andExpect(jsonPath("$[0].categoria", is("Belleza")))
                .andExpect(jsonPath("$[0].rating", is(4.5)))
                .andExpect(jsonPath("$[0].totalResenas", is(25)))
                .andExpect(jsonPath("$[1].nombre", is("Barberia Premium")))
                .andExpect(jsonPath("$[1].rating", is(4.8)));
    }

    @Test
    @DisplayName("GET /api/businesses - Should return empty list when no businesses")
    void testGetBusinessesEmpty() throws Exception {
        when(businessService.getCards()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/businesses - Should return business with correct schedule")
    void testGetBusinessesWithSchedule() throws Exception {
        List<BusinessCardResponse> businesses = Arrays.asList(business1);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].horarioHoy", is("09:00 - 18:00")));
    }

    @Test
    @DisplayName("GET /api/businesses - Should return business with closed status")
    void testGetBusinessesClosed() throws Exception {
        BusinessCardResponse closedBusiness = new BusinessCardResponse(
                UUID.randomUUID(),
                "Tienda Cerrada",
                "Retail",
                "Calle Cerrada 789",
                "https://example.com/logo3.png",
                0.0,
                0,
                "Cerrado hoy"
        );
        List<BusinessCardResponse> businesses = Arrays.asList(closedBusiness);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].horarioHoy", is("Cerrado hoy")));
    }

    @Test
    @DisplayName("GET /api/businesses - Should return business with logo URL")
    void testGetBusinessesWithLogo() throws Exception {
        List<BusinessCardResponse> businesses = Arrays.asList(business1);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].logoUrl", is("https://example.com/logo1.png")));
    }

    @Test
    @DisplayName("GET /api/businesses - Should return businesses sorted by name")
    void testGetBusinessesSorted() throws Exception {
        BusinessCardResponse barber = new BusinessCardResponse(
                UUID.randomUUID(),
                "Barberia",
                "Barberia",
                "Dir 1",
                "url",
                4.0,
                10,
                "09:00 - 18:00"
        );
        BusinessCardResponse salon = new BusinessCardResponse(
                UUID.randomUUID(),
                "Salon",
                "Salon",
                "Dir 2",
                "url",
                4.0,
                10,
                "09:00 - 18:00"
        );
        List<BusinessCardResponse> businesses = Arrays.asList(barber, salon);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Barberia")))
                .andExpect(jsonPath("$[1].nombre", is("Salon")));
    }

    @Test
    @DisplayName("GET /api/businesses - Should return business with direccion")
    void testGetBusinessesWithDireccion() throws Exception {
        List<BusinessCardResponse> businesses = Arrays.asList(business1);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].direccion", is("Calle Principal 123")));
    }

    @Test
    @DisplayName("GET /api/businesses - Should have correct response format")
    void testGetBusinessesResponseFormat() throws Exception {
        List<BusinessCardResponse> businesses = Arrays.asList(business1);
        when(businessService.getCards()).thenReturn(businesses);

        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isMap())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].nombre").exists())
                .andExpect(jsonPath("$[0].categoria").exists())
                .andExpect(jsonPath("$[0].direccion").exists())
                .andExpect(jsonPath("$[0].logoUrl").exists())
                .andExpect(jsonPath("$[0].rating").exists())
                .andExpect(jsonPath("$[0].totalResenas").exists())
                .andExpect(jsonPath("$[0].horarioHoy").exists());
    }
}
