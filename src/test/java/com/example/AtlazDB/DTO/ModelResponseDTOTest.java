package com.example.AtlazDB.DTO;
import com.example.AtlazDB.dto.ModelResponseDTO;
import com.example.AtlazDB.model.Model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ModelResponseDTOTest {
    @Test
    @DisplayName("Should correctly map all fields from Model entity to ModelResponseDTO")
    void shouldMapModelToModelResponseDTO() {
        // Arrange
        Model mockModel = mock(Model.class);
        when(mockModel.getId()).thenReturn(10L);
        when(mockModel.getModelName()).thenReturn("Civic");
        when(mockModel.getBrandName()).thenReturn("Honda");

        //Act
        ModelResponseDTO dto = new ModelResponseDTO(mockModel);

        //Assert
        assertEquals(10L, dto.id());
        assertEquals("Civic", dto.modelName());
        assertEquals("Honda", dto.brandName());
    }
    
}
