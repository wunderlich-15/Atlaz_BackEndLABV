package com.example.AtlazDB.DTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.AtlazDB.dto.CityResponseDTO;
import com.example.AtlazDB.model.City;

public class CityResponseDTOTest {
    @Test
    @DisplayName("Should correctly map all fields from City entity to CityResponseDTO")
    void shouldMapCityToCityResponseDTO() {
        // Arrange
        City mockCity = mock(City.class);
        when(mockCity.getId()).thenReturn(1L);
        when(mockCity.getName()).thenReturn("São Paulo");
        when(mockCity.getUf()).thenReturn("SP");

        // Act
        CityResponseDTO dto = new CityResponseDTO(mockCity);

        // Assert
        assertEquals(1L, dto.id());
        assertEquals("São Paulo", dto.nome());
        assertEquals("SP", dto.uf());
    }
    
}
