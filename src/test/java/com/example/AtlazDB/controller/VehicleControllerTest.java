package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.service.VehicleService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    private VehicleResponseDTO mockResponseDTO;

    @BeforeEach
    void setUp() {
        mockResponseDTO = mock(VehicleResponseDTO.class);
        when(mockResponseDTO.id()).thenReturn(1L);
        when(mockResponseDTO.prefix()).thenReturn("ABC-1234");
    }

    @Nested
    @DisplayName("Tests for GET endpoints")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return list of vehicles with status 200 OK")
        void shouldReturnAllVehicles() throws Exception {
            when(vehicleService.listAll()).thenReturn(List.of(mockResponseDTO));

            mockMvc.perform(get("/vehicles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].prefix").value("ABC-1234"));

            verify(vehicleService, times(1)).listAll();
        }

        @Test
        @DisplayName("Should return vehicle by ID with status 200 OK")
        void shouldFindVehicleById() throws Exception {
            when(vehicleService.findById(1L)).thenReturn(mockResponseDTO);

            mockMvc.perform(get("/vehicles/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.prefix").value("ABC-1234"));

            verify(vehicleService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should list available vehicles by user ID")
        void shouldListAvailableVehiclesByUser() throws Exception {
            when(vehicleService.findAvailableVehiclesByUser(10L)).thenReturn(List.of(mockResponseDTO));

            mockMvc.perform(get("/vehicles/available/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));

            verify(vehicleService, times(1)).findAvailableVehiclesByUser(10L);
        }

        @Test
        @DisplayName("Should get vehicle statuses array")
        void shouldGetVehicleStatuses() throws Exception {
            mockMvc.perform(get("/vehicles/vehicle-status"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Should calculate and return average consumption")
        void shouldGetAverageConsumption() throws Exception {
            when(vehicleService.calculateAverageConsumption(1L)).thenReturn(12.5);

            mockMvc.perform(get("/vehicles/1/average-consumption"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("12.5"));

            verify(vehicleService, times(1)).calculateAverageConsumption(1L);
        }
    }

    @Nested
    @DisplayName("Tests for Mutation and Patch endpoints")
    class MutationEndpointsTests {

        @Test
        @DisplayName("Should create vehicle successfully and return 200 OK")
        void shouldCreateVehicle() throws Exception {
            VehicleRequestDTO requestDTO = new VehicleRequestDTO();
            requestDTO.setPrefix("XYZ-9876");

            when(vehicleService.save(any(VehicleRequestDTO.class))).thenReturn(mockResponseDTO);

            mockMvc.perform(post("/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(vehicleService, times(1)).save(any(VehicleRequestDTO.class));
        }

        @Test
        @DisplayName("Should update vehicle successfully and return 200 OK")
        void shouldUpdateVehicle() throws Exception {
            VehicleRequestDTO requestDTO = new VehicleRequestDTO();
            requestDTO.setPrefix("XYZ-9876");

            when(vehicleService.update(eq(1L), any(VehicleRequestDTO.class))).thenReturn(mockResponseDTO);

            mockMvc.perform(put("/vehicles/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(vehicleService, times(1)).update(eq(1L), any(VehicleRequestDTO.class));
        }

        @Test
        @DisplayName("Should update oil change km via PATCH and return 200 OK")
        void shouldAtualizarKmTrocaOleo() throws Exception {
            BigDecimal km = new BigDecimal("30000.0");

            when(vehicleService.atualizarKmTrocaOleo(eq(1L), any(BigDecimal.class))).thenReturn(mockResponseDTO);

            mockMvc.perform(patch("/vehicles/1/km-troca-oleo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(km)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(vehicleService, times(1)).atualizarKmTrocaOleo(eq(1L), any(BigDecimal.class));
        }

        @Test
        @DisplayName("Should delete vehicle successfully and return 204 No Content")
        void shouldDeleteVehicle() throws Exception {
            doNothing().when(vehicleService).delete(1L);

            mockMvc.perform(delete("/vehicles/1"))
                    .andExpect(status().isNoContent());

            verify(vehicleService, times(1)).delete(1L);
        }
    }
}