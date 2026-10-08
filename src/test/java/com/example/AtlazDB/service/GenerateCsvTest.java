package com.example.AtlazDB.service;

import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenerateCsvTest {

    private GenerateCsv generateCsv;

    @BeforeEach
    void setUp() {
        generateCsv = new GenerateCsv();
    }

    @Test
    @DisplayName("Should generate CSV with matched ServiceOrder and Refueling, testing escaping logic")
    void shouldGenerateCsvWithMatchedOrderAndRefueling() {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setPrefix("ABC-1234");

        ServiceOrder order = new ServiceOrder();
        order.setId(1L);
        order.setVehicle(vehicle);
        order.setServiceType(OccurrenceType.TRANSLADO);
        order.setJustification("Need \"special\" check, with commas\nand newlines");
        order.setRequester("John Doe");
        order.setDestinationLocation("Central Station");
        order.setDepartureKm(new BigDecimal("100.0"));
        order.setArrivalKm(new BigDecimal("150.0"));
        order.setDepartureDate(LocalDateTime.of(2026, 10, 1, 8, 0));
        order.setReturnDate(LocalDateTime.of(2026, 10, 1, 18, 0));

        Refueling refueling = new Refueling();
        refueling.setId(10L);
        refueling.setServiceOrder(order);
        refueling.setLiters(new BigDecimal("20.5"));
        refueling.setTotalValue(new BigDecimal("110.0"));
        refueling.setReceiptNumber("REC-001");

        // Act
        byte[] csvBytes = generateCsv.generateCSV(List.of(order), List.of(refueling));
        String result = new String(csvBytes, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("vehicle,service_type,justification"));
        assertTrue(result.contains("ABC-1234"));
        assertTrue(result.contains("\"Need \"\"special\"\" check, with commas\nand newlines\""));
        assertTrue(result.contains("REC-001"));
    }

    @Test
    @DisplayName("Should generate CSV with standalone Refueling and null vehicle fallback")
    void shouldGenerateCsvWithStandaloneRefuelingAndNullVehicle() {
        // Arrange
        Refueling standaloneWithVehicle = new Refueling();
        Vehicle vehicle = new Vehicle();
        vehicle.setPrefix("XYZ-9999");
        standaloneWithVehicle.setVehicle(vehicle);
        standaloneWithVehicle.setDateTime(LocalDateTime.of(2026, 10, 5, 10, 0));
        standaloneWithVehicle.setLiters(new BigDecimal("40.0"));
        standaloneWithVehicle.setTotalValue(new BigDecimal("220.0"));
        standaloneWithVehicle.setReceiptNumber("REC-002");

        Refueling standaloneWithoutVehicle = new Refueling();
        standaloneWithoutVehicle.setVehicle(null); // Testing null vehicle branch
        standaloneWithoutVehicle.setDateTime(LocalDateTime.of(2026, 10, 6, 11, 0));
        standaloneWithoutVehicle.setLiters(new BigDecimal("15.0"));
        standaloneWithoutVehicle.setTotalValue(new BigDecimal("80.0"));

        // Act
        byte[] csvBytes = generateCsv.generateCSV(
                Collections.emptyList(),
                List.of(standaloneWithVehicle, standaloneWithoutVehicle)
        );
        String result = new String(csvBytes, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("XYZ-9999"));
        assertTrue(result.contains("ABASTECIMENTO"));
        assertTrue(result.contains("REC-002"));
    }

    @Test
    @DisplayName("Should generate CSV when ServiceOrder has no associated Refueling")
    void shouldGenerateCsvWhenServiceOrderHasNoRefueling() {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setPrefix("DEF-5678");

        ServiceOrder order = new ServiceOrder();
        order.setId(2L);
        order.setVehicle(vehicle);
        order.setServiceType(OccurrenceType.TRANSLADO);

        // Act
        byte[] csvBytes = generateCsv.generateCSV(List.of(order), Collections.emptyList());
        String result = new String(csvBytes, StandardCharsets.UTF_8);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("DEF-5678"));
    }
}