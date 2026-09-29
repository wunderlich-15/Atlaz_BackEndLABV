package com.example.AtlazDB.DTO;

import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.dto.RefuelingResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RefuelingResponseDTOTest {

    @Test
    @DisplayName("Should correctly map Refueling entity to RefuelingResponseDTO via static method")
    void shouldMapRefuelingToRefuelingResponseDTO() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        Refueling mockRefueling = mock(Refueling.class);
        when(mockRefueling.getId()).thenReturn(1L);
        when(mockRefueling.getDateTime()).thenReturn(now);
        when(mockRefueling.getLiters()).thenReturn(new BigDecimal("45.50"));
        when(mockRefueling.getTotalValue()).thenReturn(new BigDecimal("250.00"));
        when(mockRefueling.getReceiptNumber()).thenReturn("REC-98765");

        // Act
        RefuelingResponseDTO dto = RefuelingResponseDTO.fromEntity(mockRefueling);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(now, dto.getDate());
        assertEquals(new BigDecimal("45.50"), dto.getLiters());
        assertEquals(new BigDecimal("250.00"), dto.getValue());
        assertEquals("REC-98765", dto.getReceiptNumber());
    }

    @Test
    @DisplayName("Should test no-args constructor and getters/setters if necessary")
    void shouldCreateEmptyRefuelingResponseDTO() {
        // Act
        RefuelingResponseDTO dto = new RefuelingResponseDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getId());
    }
}