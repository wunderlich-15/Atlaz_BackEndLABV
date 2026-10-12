package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.DashboardResponseDTO;
import com.example.AtlazDB.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should return 200 OK and dashboard response DTO")
    void shouldReturnDashboardSuccessfully() throws Exception {
        // Arrange
        DashboardResponseDTO.Metrics metrics = new DashboardResponseDTO.Metrics(
                3, 7, 4, new BigDecimal("10.0"), new BigDecimal("0.00")
        );
        DashboardResponseDTO.DataContainer container = new DashboardResponseDTO.DataContainer(
                metrics, Collections.emptyList(), Collections.emptyList()
        );
        DashboardResponseDTO mockResponse = new DashboardResponseDTO(container);

        when(dashboardService.getDashboard()).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.metrics.activeVehicles").value(3))
                .andExpect(jsonPath("$.data.metrics.totalVehicles").value(7))
                .andExpect(jsonPath("$.data.metrics.techniciansInField").value(4))
                .andExpect(jsonPath("$.data.metrics.averageConsum").value(10.0));

        verify(dashboardService, times(1)).getDashboard();
    }
}