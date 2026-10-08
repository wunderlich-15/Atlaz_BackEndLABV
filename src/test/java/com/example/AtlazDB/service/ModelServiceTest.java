package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ModelRequestDTO;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.repository.ModelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

    @Mock
    private ModelRepository repository;

    @InjectMocks
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
    @DisplayName("Tests for Read Queries")
    class ReadQueriesTests {

        @Test
        @DisplayName("Should return all models")
        void shouldReturnAllModels() {
            when(repository.findAll()).thenReturn(List.of(mockModel));

            List<Model> result = modelService.listAll();

            assertEquals(1, result.size());
            assertEquals("Corolla", result.get(0).getModelName());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return Optional containing model when found by ID")
        void shouldReturnModelWhenFoundById() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockModel));

            Optional<Model> result = modelService.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
            assertEquals("Toyota", result.get().getBrandName());
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty Optional when model not found by ID")
        void shouldReturnEmptyOptionalWhenNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            Optional<Model> result = modelService.findById(1L);

            assertTrue(result.isEmpty());
            verify(repository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Tests for save")
    class SaveTests {

        @Test
        @DisplayName("Should map DTO and save model successfully")
        void shouldSaveModelSuccessfully() {
            ModelRequestDTO dto = new ModelRequestDTO();
            dto.setNameModel("Civic");
            dto.setNameBrand("Honda");

            when(repository.save(any(Model.class))).thenAnswer(i -> {
                Model saved = i.getArgument(0);
                saved.setId(2L);
                return saved;
            });

            Model result = modelService.save(dto);

            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals("Civic", result.getModelName());
            assertEquals("Honda", result.getBrandName());
            verify(repository, times(1)).save(any(Model.class));
        }
    }

    @Nested
    @DisplayName("Tests for update and delete")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Should delete model by ID")
        void shouldDeleteModelById() {
            modelService.delete(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw RuntimeException when model is not found on update")
        void shouldThrowExceptionWhenModelNotFoundOnUpdate() {
            ModelRequestDTO dto = new ModelRequestDTO();
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> modelService.update(1L, dto));

            assertEquals("Model not found", exception.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should update model successfully when found")
        void shouldUpdateModelSuccessfully() {
            ModelRequestDTO dto = new ModelRequestDTO();
            dto.setNameModel("Corolla Cross");
            dto.setNameBrand("Toyota");

            when(repository.findById(1L)).thenReturn(Optional.of(mockModel));
            when(repository.save(any(Model.class))).thenAnswer(i -> i.getArgument(0));

            Model result = modelService.update(1L, dto);

            assertNotNull(result);
            assertEquals("Corolla Cross", result.getModelName());
            assertEquals("Toyota", result.getBrandName());
            verify(repository, times(1)).save(mockModel);
        }
    }
}