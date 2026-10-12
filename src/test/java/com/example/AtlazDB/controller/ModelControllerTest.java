package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ModelRequestDTO;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.service.ModelService;
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

@WebMvcTest(ModelController.class)
class ModelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ModelService modelService;

    private Model mockModel;

    @BeforeEach
    void setUp() {
        mockModel = new Model();
        mockModel.setId(1L);
        mockModel.setModelName("Corolla");
        mockModel.setBrandName("Toyota");
    }

    @Nested
    @DisplayName("Tests for GET endpoints")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return list of models with status 200 OK")
        void shouldReturnAllModels() throws Exception {
            when(modelService.listAll()).thenReturn(List.of(mockModel));

            mockMvc.perform(get("/models"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].modelName").value("Corolla"))
                    .andExpect(jsonPath("$[0].brandName").value("Toyota"));

            verify(modelService, times(1)).listAll();
        }

        @Test
        @DisplayName("Should return model when found by ID")
        void shouldFindModelById() throws Exception {
            when(modelService.findById(1L)).thenReturn(Optional.of(mockModel));

            mockMvc.perform(get("/models/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.modelName").value("Corolla"));

            verify(modelService, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return ok when model not found by ID")
        void shouldReturnEmptyWhenModelIdDoesNotExist() throws Exception {
            when(modelService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/models/99"))
                    .andExpect(status().isOk());

            verify(modelService, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("Tests for POST, PUT and DELETE endpoints")
    class MutationEndpointsTests {

        @Test
        @DisplayName("Should create model successfully and return 200 OK")
        void shouldCreateModel() throws Exception {
            ModelRequestDTO requestDTO = new ModelRequestDTO();
            requestDTO.setNameModel("Civic");
            requestDTO.setNameBrand("Honda");

            Model savedModel = new Model();
            savedModel.setId(2L);
            savedModel.setModelName("Civic");
            savedModel.setBrandName("Honda");

            when(modelService.save(any(ModelRequestDTO.class))).thenReturn(savedModel);

            mockMvc.perform(post("/models")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(2L))
                    .andExpect(jsonPath("$.modelName").value("Civic"))
                    .andExpect(jsonPath("$.brandName").value("Honda"));

            verify(modelService, times(1)).save(any(ModelRequestDTO.class));
        }

        @Test
        @DisplayName("Should update model successfully and return 200 OK")
        void shouldUpdateModel() throws Exception {
            ModelRequestDTO requestDTO = new ModelRequestDTO();
            requestDTO.setNameModel("Corolla Cross");
            requestDTO.setNameBrand("Toyota");

            when(modelService.update(eq(1L), any(ModelRequestDTO.class))).thenReturn(mockModel);

            mockMvc.perform(put("/models/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(modelService, times(1)).update(eq(1L), any(ModelRequestDTO.class));
        }

        @Test
        @DisplayName("Should delete model successfully and return 200 OK")
        void shouldDeleteModel() throws Exception {
            doNothing().when(modelService).delete(1L);

            mockMvc.perform(delete("/models/1"))
                    .andExpect(status().isOk());

            verify(modelService, times(1)).delete(1L);
        }
    }
}