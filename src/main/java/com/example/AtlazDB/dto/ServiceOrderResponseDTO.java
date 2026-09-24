package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.model.ServiceOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceOrderResponseDTO(
        Long id,
        OccurrenceType serviceType,
        String destinationLocation,
        String justification,
        String requester,
        BigDecimal departureKm,
        BigDecimal arrivalKm,
        LocalDateTime departureDate,
        LocalDateTime returnDate
) {
    // Constructor to convert the Entity into DTO
    public ServiceOrderResponseDTO(ServiceOrder serviceOrder) {
        this(
                serviceOrder.getId(),
                serviceOrder.getServiceType(),
                serviceOrder.getDestinationLocation(),
                serviceOrder.getJustification(),
                serviceOrder.getRequester(),
                serviceOrder.getDepartureKm(),
                serviceOrder.getArrivalKm(),
                serviceOrder.getDepartureDate(),
                serviceOrder.getReturnDate()
        );
    }
}