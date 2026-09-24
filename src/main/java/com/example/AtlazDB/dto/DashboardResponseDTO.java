package com.example.AtlazDB.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponseDTO(DataContainer data) {
    public record DataContainer(
            Metrics metrics,
            List<VehicleKm> vehicleKm,
            List<TodayActivity> todayActivities
    ){}

    public record Metrics(
            Integer activeVehicles,
            Integer totalVehicles,
            Integer techniciansInField,
            BigDecimal averageConsum,
            BigDecimal todayExpense
    ){}

    public record VehicleKm(
            String prefix,
            Integer km
    ){}

    public record TodayActivity(
            Long id,
            String type,
            String vehiclePrefix,
            String technicianName,
            String description,
            String status,
            String statusColor
    ){}
}