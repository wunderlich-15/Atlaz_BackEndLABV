package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.CityRequestDTO;
import com.example.AtlazDB.model.City;
import com.example.AtlazDB.repository.CityRepository;
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
class CityServiceTest {

    @Mock
    private CityRepository repository;

    @InjectMocks
    private CityService cityService;

    private City mockCity;

    @BeforeEach
    void setUp() {
        mockCity = new City();
        mockCity.setId(1L);
        mockCity.setName("São Paulo");
        mockCity.setUf("SP");
    }

    @Nested
    @DisplayName("Tests for Read Queries")
    class ReadQueriesTests {

        @Test
        @DisplayName("Should return all cities")
        void shouldReturnAllCities() {
            when(repository.findAll()).thenReturn(List.of(mockCity));

            List<City> result = cityService.listAll();

            assertEquals(1, result.size());
            assertEquals("São Paulo", result.get(0).getName());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return Optional containing city when found by ID")
        void shouldReturnCityWhenFoundById() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockCity));

            Optional<City> result = cityService.findById(1L);

            assertTrue(result.isPresent());
            assertEquals(1L, result.get().getId());
            assertEquals("SP", result.get().getUf());
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should return empty Optional when city not found by ID")
        void shouldReturnEmptyOptionalWhenNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            Optional<City> result = cityService.findById(1L);

            assertTrue(result.isEmpty());
            verify(repository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("Tests for save")
    class SaveTests {

        @Test
        @DisplayName("Should map DTO and save city successfully")
        void shouldSaveCitySuccessfully() {
            CityRequestDTO dto = new CityRequestDTO();
            dto.setName("Campinas");
            dto.setUf("SP");

            when(repository.save(any(City.class))).thenAnswer(i -> {
                City saved = i.getArgument(0);
                saved.setId(2L);
                return saved;
            });

            City result = cityService.save(dto);

            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals("Campinas", result.getName());
            assertEquals("SP", result.getUf());
            verify(repository, times(1)).save(any(City.class));
        }
    }

    @Nested
    @DisplayName("Tests for update and delete")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Should delete city by ID")
        void shouldDeleteCityById() {
            cityService.delete(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw RuntimeException when city is not found on update")
        void shouldThrowExceptionWhenCityNotFoundOnUpdate() {
            CityRequestDTO dto = new CityRequestDTO();
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> cityService.update(1L, dto));

            assertEquals("City not found", exception.getMessage());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should update city successfully when found")
        void shouldUpdateCitySuccessfully() {
            CityRequestDTO dto = new CityRequestDTO();
            dto.setName("Santos");
            dto.setUf("SP");

            when(repository.findById(1L)).thenReturn(Optional.of(mockCity));
            when(repository.save(any(City.class))).thenAnswer(i -> i.getArgument(0));

            City result = cityService.update(1L, dto);

            assertNotNull(result);
            assertEquals("Santos", result.getName());
            assertEquals("SP", result.getUf());
            verify(repository, times(1)).save(mockCity);
        }
    }
}