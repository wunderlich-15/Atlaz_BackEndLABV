package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ServiceOrderRequestDTO;
import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.service.GenerateCsv;
import com.example.AtlazDB.service.RefuelingService;
import com.example.AtlazDB.service.ServiceOrderService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceOrderController.class)
class ServiceOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ServiceOrderService serviceOrderService;

    @MockitoBean
    private RefuelingService refuelingService;

    @MockitoBean
    private GenerateCsv generateCsv;

    private ServiceOrder mockServiceOrder;

    @BeforeEach
    void setUp() {
        mockServiceOrder = new ServiceOrder();
        mockServiceOrder.setId(1L);
        mockServiceOrder.setServiceType(OccurrenceType.INSPECAO);
    }

    @Nested
    @DisplayName("Tests for GET endpoints")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return list of service orders")
        void shouldReturnAllServiceOrders() throws Exception {
            when(serviceOrderService.listAll()).thenReturn(List.of(mockServiceOrder));

            mockMvc.perform(get("/service-orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));

            verify(serviceOrderService, times(1)).listAll();
        }

        @Test
        @DisplayName("Should list occurrence types enum values")
        void shouldListOccurrenceTypes() throws Exception {
            mockMvc.perform(get("/service-orders/occurrence-types"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Should return service order by ID")
        void shouldFindServiceOrderById() throws Exception {
            when(serviceOrderService.findById(1L)).thenReturn(Optional.of(mockServiceOrder));

            mockMvc.perform(get("/service-orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(serviceOrderService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when service order not found by ID")
        void shouldReturnNullWhenNotFoundById() throws Exception {
            when(serviceOrderService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/service-orders/99"))
                    .andExpect(status().isOk());

            verify(serviceOrderService, times(1)).findById(99L);
        }

        @Test
        @DisplayName("Should find service orders by user ID")
        void shouldFindByUserId() throws Exception {
            when(serviceOrderService.findByUserId(10L)).thenReturn(List.of(mockServiceOrder));

            mockMvc.perform(get("/service-orders/user/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));

            verify(serviceOrderService, times(1)).findByUserId(10L);
        }

        @Test
        @DisplayName("Should find service orders by month and year")
        void shouldFindByMonthAndYear() throws Exception {
            when(serviceOrderService.findByMonthAndYear(10, 2026)).thenReturn(List.of(mockServiceOrder));

            mockMvc.perform(get("/service-orders/by-month")
                            .param("month", "10")
                            .param("year", "2026"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L));

            verify(serviceOrderService, times(1)).findByMonthAndYear(10, 2026);
        }

        @Test
        @DisplayName("Should count service orders by date interval")
        void shouldCountByInterval() throws Exception {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 12);

            when(serviceOrderService.countServiceOrdersByDateInterval(start, end)).thenReturn(3L);

            mockMvc.perform(get("/service-orders/count-by-interval")
                            .param("dataInicio", "2026-10-01")
                            .param("dataFim", "2026-10-12"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("3"));

            verify(serviceOrderService, times(1)).countServiceOrdersByDateInterval(start, end);
        }
    }

    @Nested
    @DisplayName("Tests for CSV Download endpoint branches")
    class CsvDownloadTests {

        @Test
        @DisplayName("Should download CSV with date interval filter")
        void shouldDownloadCsvWithDateInterval() throws Exception {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 12);
            byte[] csvMockData = "id,description\n1,Maintenance".getBytes();

            when(serviceOrderService.findByInterval(start, end)).thenReturn(List.of(mockServiceOrder));
            when(refuelingService.findByInterval(start, end)).thenReturn(Collections.emptyList());
            when(generateCsv.generateCSV(any(), any())).thenReturn(csvMockData);

            mockMvc.perform(get("/service-orders/csv")
                            .param("dataInicio", "2026-10-01")
                            .param("dataFim", "2026-10-12"))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition", "attachment; filename=records.csv"))
                    .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                    .andExpect(content().bytes(csvMockData));
        }

        @Test
        @DisplayName("Should download CSV with month and year filter and category 'abastecimento'")
        void shouldDownloadCsvWithMonthYearAndCategoryAbastecimento() throws Exception {
            byte[] csvMockData = "csv,data".getBytes();

            when(serviceOrderService.findByMonthAndYear(10, 2026)).thenReturn(List.of(mockServiceOrder));
            when(refuelingService.findByMonthAndYear(10, 2026)).thenReturn(Collections.emptyList());
            when(generateCsv.generateCSV(any(), any())).thenReturn(csvMockData);

            mockMvc.perform(get("/service-orders/csv")
                            .param("month", "10")
                            .param("year", "2026")
                            .param("categoria", "abastecimento"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should download CSV with default list and category 'os'")
        void shouldDownloadCsvDefaultAndCategoryOs() throws Exception {
            byte[] csvMockData = "csv,data".getBytes();

            when(serviceOrderService.listAll()).thenReturn(List.of(mockServiceOrder));
            when(refuelingService.listAll()).thenReturn(Collections.emptyList());
            when(generateCsv.generateCSV(any(), any())).thenReturn(csvMockData);

            mockMvc.perform(get("/service-orders/csv")
                            .param("categoria", "os"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Tests for POST, PUT and DELETE endpoints")
    class MutationEndpointsTests {

        @Test
        @DisplayName("Should create service order successfully")
        void shouldCreateServiceOrder() throws Exception {
            ServiceOrderRequestDTO requestDTO = new ServiceOrderRequestDTO();
            requestDTO.setJustification("Engine maintenance");
            requestDTO.setRequester("John Doe");
            requestDTO.setServiceType(OccurrenceType.INSPECAO);

            when(serviceOrderService.save(any(ServiceOrderRequestDTO.class))).thenReturn(mockServiceOrder);

            mockMvc.perform(post("/service-orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(serviceOrderService, times(1)).save(any(ServiceOrderRequestDTO.class));
        }

        @Test
        @DisplayName("Should update service order successfully")
        void shouldUpdateServiceOrder() throws Exception {
            ServiceOrderRequestDTO requestDTO = new ServiceOrderRequestDTO();
            requestDTO.setJustification("Updated maintenance");
            requestDTO.setRequester("Jane Doe");
            requestDTO.setServiceType(OccurrenceType.INSPECAO);

            when(serviceOrderService.update(eq(1L), any(ServiceOrderRequestDTO.class)))
                    .thenReturn(mockServiceOrder);

            mockMvc.perform(put("/service-orders/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(serviceOrderService, times(1)).update(eq(1L), any(ServiceOrderRequestDTO.class));
        }

        @Test
        @DisplayName("Should delete service order successfully")
        void shouldDeleteServiceOrder() throws Exception {
            doNothing().when(serviceOrderService).delete(1L);

            mockMvc.perform(delete("/service-orders/1"))
                    .andExpect(status().isOk());

            verify(serviceOrderService, times(1)).delete(1L);
        }
    }
}