package com.example.AtlazDB.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.AtlazDB.model.Refueling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefuelingResponseDTO {

    private Long id;
    private LocalDateTime date;
    private BigDecimal liters;
    private BigDecimal value;
    private String receiptNumber;

    public static RefuelingResponseDTO fromEntity(Refueling refueling) {
        return new RefuelingResponseDTO(
            refueling.getId(),
            refueling.getDateTime(),
            refueling.getLiters(),
            refueling.getTotalValue(),
            refueling.getReceiptNumber()
        );
    }
}