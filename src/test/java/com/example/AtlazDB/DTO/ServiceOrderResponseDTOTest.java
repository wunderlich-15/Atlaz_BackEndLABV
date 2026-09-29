package com.example.AtlazDB.DTO;

import com.example.AtlazDB.dto.ServiceOrderResponseDTO;

import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.model.ServiceOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServiceOrderResponseDTOTest {

    @Test
    @DisplayName("Should correctly map all fields from Service Order entity to ServiceOrderResponseDTO")

    void ShouldMapSOToServiceOrderResponseDTO(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime returnTime = now.plusDays(1);
        //Arrange
        ServiceOrder mockOrder = mock(ServiceOrder.class);
        when(mockOrder.getId()).thenReturn(123L);
        when(mockOrder.getServiceType()).thenReturn(OccurrenceType.TRANSLADO);
        when(mockOrder.getDestinationLocation()).thenReturn("New Orleans");
        when(mockOrder.getJustification()).thenReturn("Work");
        when(mockOrder.getRequester()).thenReturn("John Doe");
        when(mockOrder.getDepartureKm()).thenReturn(new BigDecimal("10000.0"));
        when(mockOrder.getArrivalKm()).thenReturn(new BigDecimal("10000.0"));
        when(mockOrder.getDepartureDate()).thenReturn(now);
        when(mockOrder.getReturnDate()).thenReturn(returnTime);

        //Act 
        ServiceOrderResponseDTO dto = new ServiceOrderResponseDTO(mockOrder);

        //Arrange
        assertEquals(123L, dto.id());
        assertEquals(OccurrenceType.TRANSLADO, dto.serviceType());
        assertEquals("New Orleans", dto.destinationLocation());
        assertEquals("Work", dto.justification());
        assertEquals("John Doe", dto.requester());
        assertEquals(new BigDecimal("10000.0"), dto.departureKm());
        assertEquals(new BigDecimal("10000.0"), dto.arrivalKm());
        assertEquals(now, dto.departureDate());
        assertEquals(returnTime, dto.returnDate());

    }
    
}
