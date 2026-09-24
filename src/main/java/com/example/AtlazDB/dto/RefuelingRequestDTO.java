package com.example.AtlazDB.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class RefuelingRequestDTO {
    private LocalDateTime dateTime;
    private BigDecimal liters;
    private BigDecimal totalValue;
    private String receiptNumber;
    private Long userId;
    private Long vehicleId;
    private Long cityId;
    private Long serviceOrderId;

}