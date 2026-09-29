package com.example.AtlazDB.DTO;

import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.FuelType;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.enums.VehicleType;
import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VehicleResponseDTOTest {

    @Test
    @DisplayName("Should correctly map all fields from Vehicle entity to VehicleResponseDTO")
    void shouldMapVehicleToVehicleResponseDTO() {
        // Arrange
        Model mockModel = mock(Model.class);
        when(mockModel.getId()).thenReturn(10L);
        when(mockModel.getModelName()).thenReturn("Civic");
        when(mockModel.getBrandName()).thenReturn("Honda");

        Vehicle mockVehicle = mock(Vehicle.class);
        when(mockVehicle.getId()).thenReturn(1L);
        when(mockVehicle.getPrefix()).thenReturn("ABC-1234");
        when(mockVehicle.getModel()).thenReturn(mockModel);
        when(mockVehicle.getVehicleStatus()).thenReturn(VehicleStatus.DISPONIVEL);
        when(mockVehicle.getFuelType()).thenReturn(FuelType.GASOLINA);
        when(mockVehicle.getType()).thenReturn(VehicleType.PASSEIO);
        when(mockVehicle.getKm()).thenReturn(50000.0);
        when(mockVehicle.getTipoCnhNecessaria()).thenReturn(CnhType.B);
        when(mockVehicle.getKmTrocaOleo()).thenReturn(new BigDecimal("10000.00"));

        // Act (Execução do construtor testado)
        VehicleResponseDTO dto = new VehicleResponseDTO(mockVehicle);

        // Assert (Validação de 100% dos campos)
        assertEquals(1L, dto.id());
        assertEquals("ABC-1234", dto.prefix());
        assertEquals("Civic", dto.model());
        assertEquals("Honda", dto.brand());
        assertEquals(VehicleStatus.DISPONIVEL, dto.status());
        assertEquals(FuelType.GASOLINA, dto.fuelType());
        assertEquals(VehicleType.PASSEIO, dto.type());
        assertEquals(50000.0, dto.km());
        assertEquals(10L, dto.modelId());
        assertEquals(CnhType.B, dto.tipoCnhNecessaria());
        assertEquals(new BigDecimal("10000.00"), dto.kmTrocaOleo());
    }
}