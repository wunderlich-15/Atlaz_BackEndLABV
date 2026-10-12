package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.CityRequestDTO;
import com.example.AtlazDB.model.City;
import com.example.AtlazDB.service.CityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CityService cityService;

    private City mockCity;

    @BeforeEach
    void setUp() {
        mockCity = new City();
        mockCity.setId(1L);
        mockCity.setName("São José dos Campos");
        mockCity.setUf("SP");
    }

    @Nested
    @DisplayName("Tests for GET endpoints")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return list of cities with status 200 OK")
        void shouldReturnAllCities() throws Exception {
            when(cityService.listAll()).thenReturn(List.of(mockCity));

            mockMvc.perform(get("/cities"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("São José dos Campos"))
                    .andExpect(jsonPath("$[0].uf").value("SP"));

            verify(cityService, times(1)).listAll();
        }

        @Test
        @DisplayName("Should return city when found by ID")
        void shouldFindCityById() throws Exception {
            when(cityService.findById(1L)).thenReturn(Optional.of(mockCity));

            mockMvc.perform(get("/cities/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("São José dos Campos"));

            verify(cityService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty or null when city not found by ID")
        void shouldReturnNotFoundWhenCityIdDoesNotExist() throws Exception {
            when(cityService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/cities/99"))
                    .andExpect(status().isOk()); // Controller returns null via .orElse(null)

            verify(cityService, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("Tests for POST, PUT and DELETE endpoints")
    class MutationEndpointsTests {

        @Test
        @DisplayName("Should create city successfully and return 200 OK")
        void shouldCreateCity() throws Exception {
            CityRequestDTO requestDTO = new CityRequestDTO();
            requestDTO.setName("Campinas");
            requestDTO.setUf("SP");

            City savedCity = new City();
            savedCity.setId(2L);
            savedCity.setName("Campinas");
            savedCity.setUf("SP");

            when(cityService.save(any(CityRequestDTO.class))).thenReturn(savedCity);

            mockMvc.perform(post("/cities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2L))
                    .andExpect(jsonPath("$.name").value("Campinas"))
                    .andExpect(jsonPath("$.uf").value("SP"));

            verify(cityService, times(1)).save(any(CityRequestDTO.class));
        }

        @Test
        @DisplayName("Should update city successfully and return 200 OK")
        void shouldUpdateCity() throws Exception {
            CityRequestDTO requestDTO = new CityRequestDTO();
            requestDTO.setName("São Paulo");
            requestDTO.setUf("SP");

            when(cityService.update(eq(1L), any(CityRequestDTO.class))).thenReturn(mockCity);

            mockMvc.perform(put("/cities/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("São José dos Campos"));

            verify(cityService, times(1)).update(eq(1L), any(CityRequestDTO.class));
        }

        @Test
        @DisplayName("Should delete city successfully and return 200 OK")
        void shouldDeleteCity() throws Exception {
            doNothing().when(cityService).delete(1L);

            mockMvc.perform(delete("/cities/1"))
                    .andExpect(status().isOk());

            verify(cityService, times(1)).delete(1L);
        }
    }
}