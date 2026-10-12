package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.RefuelingRequestDTO;
import com.example.AtlazDB.enums.FuelType;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.service.RefuelingService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RefuelingController.class)
class RefuelingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RefuelingService refuelingService;

    @MockitoBean
    private com.example.AtlazDB.service.GenerateCsv generateCsv; // Adicionado para satisfazer o construtor do Controller

    private Refueling mockRefueling;

    @BeforeEach
    void setUp() {
        mockRefueling = new Refueling();
        mockRefueling.setId(1L);
        mockRefueling.setFuelType(FuelType.GASOLINA);
        mockRefueling.setTotalValue(new BigDecimal("150.00"));
        mockRefueling.setCurrentKm(new BigDecimal("25000.0"));
    }

    @Nested
    @DisplayName("Tests for GET endpoints and filters")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return list of refuelings with optional query parameters")
        void shouldSearchRefuelingsWithFilters() throws Exception {
            when(refuelingService.searchWithFilters(FuelType.GASOLINA, 150.0, 25000.0))
                    .thenReturn(List.of(mockRefueling));

            mockMvc.perform(get("/refuelings")
                            .param("fuelType", "GASOLINA")
                            .param("totalValue", "150.0")
                            .param("currentKm", "25000.0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].fuelType").value("GASOLINA"));

            verify(refuelingService, times(1))
                    .searchWithFilters(FuelType.GASOLINA, 150.0, 25000.0);
        }

        @Test
        @DisplayName("Should return refueling when found by ID")
        void shouldFindRefuelingById() throws Exception {
            when(refuelingService.findById(1L)).thenReturn(Optional.of(mockRefueling));

            mockMvc.perform(get("/refuelings/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(refuelingService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return list by month and year")
        void shouldFindByMonthAndYear() throws Exception {
            when(refuelingService.findByMonthAndYear(10, 2026))
                    .thenReturn(List.of(mockRefueling));

            mockMvc.perform(get("/refuelings/by-month")
                            .param("month", "10")
                            .param("year", "2026"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));

            verify(refuelingService, times(1)).findByMonthAndYear(10, 2026);
        }

        @Test
        @DisplayName("Should count refuelings by date interval")
        void shouldCountRefuelingsByDate() throws Exception {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 12);

            when(refuelingService.countRefuelingsByDateInterval(start, end)).thenReturn(5L);

            mockMvc.perform(get("/refuelings/count-by-date")
                            .param("startDate", "2026-10-01")
                            .param("endDate", "2026-10-12"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("5"));

            verify(refuelingService, times(1))
                    .countRefuelingsByDateInterval(start, end);
        }
    }

    @Nested
    @DisplayName("Tests for POST, PUT and DELETE endpoints")
    class MutationEndpointsTests {

        @Test
        @DisplayName("Should create refueling successfully")
        void shouldCreateRefueling() throws Exception {
            RefuelingRequestDTO requestDTO = new RefuelingRequestDTO();
            requestDTO.setLiters(new BigDecimal("50.0"));
            requestDTO.setTotalValue(new BigDecimal("150.00"));

            when(refuelingService.save(any(RefuelingRequestDTO.class))).thenReturn(mockRefueling);

            mockMvc.perform(post("/refuelings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(refuelingService, times(1)).save(any(RefuelingRequestDTO.class));
        }

        @Test
        @DisplayName("Should update refueling successfully")
        void shouldUpdateRefueling() throws Exception {
            RefuelingRequestDTO requestDTO = new RefuelingRequestDTO();
            requestDTO.setLiters(new BigDecimal("50.0"));
            requestDTO.setTotalValue(new BigDecimal("150.00"));

            when(refuelingService.update(eq(1L), any(RefuelingRequestDTO.class)))
                    .thenReturn(mockRefueling);

            mockMvc.perform(put("/refuelings/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(refuelingService, times(1)).update(eq(1L), any(RefuelingRequestDTO.class));
        }

        @Test
        @DisplayName("Should delete refueling successfully")
        void shouldDeleteRefueling() throws Exception {
            doNothing().when(refuelingService).delete(1L);

            mockMvc.perform(delete("/refuelings/1"))
                    .andExpect(status().isOk());

            verify(refuelingService, times(1)).delete(1L);
        }
    }
}