package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.enums.VehicleType;
import com.example.AtlazDB.service.BusinessRuleException;
import com.example.AtlazDB.model.*;
import com.example.AtlazDB.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefuelingRepository refuelingRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Model mockModel;
    private Vehicle mockVehicle;

    @BeforeEach
    void setUp() {
        mockModel = new Model();
        mockModel.setId(10L);
        mockModel.setModelName("Civic");
        mockModel.setBrandName("Honda");

        mockVehicle = new Vehicle();
        mockVehicle.setId(1L);
        mockVehicle.setPrefix("ABC-1234");
        mockVehicle.setModel(mockModel);
        mockVehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        mockVehicle.setTipoCnhNecessaria(CnhType.B);
        mockVehicle.setKm(50000.0);
        mockVehicle.setKmTrocaOleo(new BigDecimal("60000"));
    }

    @Nested
    @DisplayName("Tests for findAvailableVehiclesByUser")
    class FindAvailableVehiclesByUserTests {

        @Test
        @DisplayName("Should throw exception when user is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.findAvailableVehiclesByUser(1L));

            assertEquals("User not found!", exception.getMessage());
        }

        @Test
        @DisplayName("Should return available vehicles compatible with user CNH")
        void shouldReturnAvailableVehiclesCompatibleWithCnh() {
            User mockUser = new User();
            mockUser.setId(1L);
            mockUser.setCnhTypes(Set.of(CnhType.B));

            Vehicle unavailableVehicle = new Vehicle();
            unavailableVehicle.setId(2L);
            unavailableVehicle.setModel(mockModel);
            unavailableVehicle.setVehicleStatus(VehicleStatus.DESATIVADA);
            unavailableVehicle.setTipoCnhNecessaria(CnhType.B);

            Vehicle incompatibleCnhVehicle = new Vehicle();
            incompatibleCnhVehicle.setId(3L);
            incompatibleCnhVehicle.setModel(mockModel);
            incompatibleCnhVehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
            incompatibleCnhVehicle.setTipoCnhNecessaria(CnhType.D);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(repository.findAll()).thenReturn(List.of(mockVehicle, unavailableVehicle, incompatibleCnhVehicle));

            List<VehicleResponseDTO> result = vehicleService.findAvailableVehiclesByUser(1L);

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).id());
        }
    }

    @Nested
    @DisplayName("Tests for listAll and findById")
    class FindAndListTests {

        @Test
        @DisplayName("Should return all vehicles")
        void shouldListAllVehicles() {
            when(repository.findAll()).thenReturn(List.of(mockVehicle));

            List<VehicleResponseDTO> result = vehicleService.listAll();

            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).id());
        }

        @Test
        @DisplayName("Should return vehicle when found by ID")
        void shouldFindVehicleById() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockVehicle));

            VehicleResponseDTO result = vehicleService.findById(1L);

            assertNotNull(result);
            assertEquals(1L, result.id());
        }

        @Test
        @DisplayName("Should throw exception when vehicle not found by ID")
        void shouldThrowExceptionWhenVehicleNotFoundById() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.findById(1L));

            assertEquals("Vehicle not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for save")
    class SaveTests {

        @Test
        @DisplayName("Should throw exception when model is not found during save")
        void shouldThrowExceptionWhenModelNotFound() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            dto.setModelId(99L);

            when(modelRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.save(dto));

            assertEquals("Model not found!", exception.getMessage());
        }

        @Test
        @DisplayName("Should save vehicle with all custom values provided")
        void shouldSaveVehicleWithCustomValues() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            dto.setModelId(10L);
            dto.setPrefix("DEF-5678");
            dto.setType(VehicleType.PASSEIO);
            dto.setStatus(VehicleStatus.MANUTENCAO);
            dto.setTipoCnhNecessaria(CnhType.C);
            dto.setKm(15000.0);
            dto.setKmTrocaOleo(25000.0);

            when(modelRepository.findById(10L)).thenReturn(Optional.of(mockModel));
            when(repository.save(any(Vehicle.class))).thenAnswer(i -> {
                Vehicle v = i.getArgument(0);
                v.setId(2L);
                return v;
            });

            VehicleResponseDTO result = vehicleService.save(dto);

            assertNotNull(result);
            assertEquals(2L, result.id());
            assertEquals("DEF-5678", result.prefix());
            assertEquals(VehicleStatus.MANUTENCAO, result.status());
            assertEquals(CnhType.C, result.tipoCnhNecessaria());
            assertEquals(15000.0, result.km());
            assertEquals(new BigDecimal("25000.0"), result.kmTrocaOleo());
        }

        @Test
        @DisplayName("Should save vehicle applying default fallback values when fields are null")
        void shouldSaveVehicleWithDefaultFallbackValues() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            dto.setModelId(10L);
            dto.setPrefix("XYZ-9999");
            // Fields like status, tipoCnhNecessaria, km, kmTrocaOleo are left null

            when(modelRepository.findById(10L)).thenReturn(Optional.of(mockModel));
            when(repository.save(any(Vehicle.class))).thenAnswer(i -> {
                Vehicle v = i.getArgument(0);
                v.setId(3L);
                return v;
            });

            VehicleResponseDTO result = vehicleService.save(dto);

            assertNotNull(result);
            assertEquals(VehicleStatus.DISPONIVEL, result.status());
            assertEquals(CnhType.B, result.tipoCnhNecessaria());
            assertEquals(0.0, result.km());
            assertEquals(new BigDecimal("10000.0"), result.kmTrocaOleo());
        }
    }

    @Nested
    @DisplayName("Tests for update and delete")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Should delete vehicle by ID")
        void shouldDeleteVehicle() {
            vehicleService.delete(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing vehicle")
        void shouldThrowExceptionWhenUpdatingNonExistingVehicle() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.update(1L, dto));

            assertEquals("Vehicle not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when updating with non-existing model")
        void shouldThrowExceptionWhenUpdatingNonExistingModel() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            dto.setModelId(99L);

            when(repository.findById(1L)).thenReturn(Optional.of(mockVehicle));
            when(modelRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.update(1L, dto));

            assertEquals("Model not found!", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when disabling vehicle with active service order")
        void shouldThrowExceptionWhenDisablingVehicleWithActiveOrder() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            dto.setModelId(10L);
            dto.setStatus(VehicleStatus.DESATIVADA);

            when(repository.findById(1L)).thenReturn(Optional.of(mockVehicle));
            when(modelRepository.findById(10L)).thenReturn(Optional.of(mockModel));
            when(serviceOrderRepository.existsByVehicleIdAndReturnDateIsNull(1L)).thenReturn(true);

            BusinessRuleException exception = assertThrows(BusinessRuleException.class,
                    () -> vehicleService.update(1L, dto));

            assertTrue(exception.getMessage().contains("Não é possível inativar a viatura"));
        }

        @Test
        @DisplayName("Should successfully update vehicle with valid non-null fields")
        void shouldUpdateVehicleSuccessfully() {
            VehicleRequestDTO dto = new VehicleRequestDTO();
            dto.setModelId(10L);
            dto.setPrefix("NEW-1234");
            dto.setType(VehicleType.PASSEIO);
            dto.setStatus(VehicleStatus.MANUTENCAO);
            dto.setKm(60000.0);
            dto.setTipoCnhNecessaria(CnhType.C);

            when(repository.findById(1L)).thenReturn(Optional.of(mockVehicle));
            when(modelRepository.findById(10L)).thenReturn(Optional.of(mockModel));
            when(repository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

            VehicleResponseDTO result = vehicleService.update(1L, dto);

            assertNotNull(result);
            assertEquals("NEW-1234", result.prefix());
            assertEquals(VehicleStatus.MANUTENCAO, result.status());
            assertEquals(60000.0, result.km());
            assertEquals(CnhType.C, result.tipoCnhNecessaria());
        }
    }

    @Nested
    @DisplayName("Tests for updateCurrentKm")
    class UpdateCurrentKmTests {

        @Test
        @DisplayName("Should update vehicle KM when last service order has arrival KM")
        void shouldUpdateCurrentKmWhenLastOrderHasArrivalKm() {
            ServiceOrder lastOrder = new ServiceOrder();
            lastOrder.setArrivalKm(new BigDecimal("75000.00"));

            when(serviceOrderRepository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(1L))
                    .thenReturn(lastOrder);
            when(repository.findById(1L)).thenReturn(Optional.of(mockVehicle));

            vehicleService.updateCurrentKm(1L);

            assertEquals(75000.00, mockVehicle.getKm());
            verify(repository, times(1)).save(mockVehicle);
        }

        @Test
        @DisplayName("Should do nothing when last service order is null or arrival KM is null")
        void shouldDoNothingWhenLastOrderOrArrivalKmIsNull() {
            when(serviceOrderRepository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(1L))
                    .thenReturn(null);

            vehicleService.updateCurrentKm(1L);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when vehicle is not found during KM update")
        void shouldThrowExceptionWhenVehicleNotFoundForKmUpdate() {
            ServiceOrder lastOrder = new ServiceOrder();
            lastOrder.setArrivalKm(new BigDecimal("75000.00"));

            when(serviceOrderRepository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(1L))
                    .thenReturn(lastOrder);
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.updateCurrentKm(1L));

            assertEquals("Vehicle not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Tests for calculateAverageConsumption")
    class CalculateAverageConsumptionTests {

        @Test
        @DisplayName("Should return 0.0 when refueling list is empty or has no valid entries")
        void shouldReturnZeroWhenNoValidRefuelingEntries() {
            when(refuelingRepository.findByVehicleId(1L)).thenReturn(Collections.emptyList());

            Double average = vehicleService.calculateAverageConsumption(1L);

            assertEquals(0.0, average);
        }

        @Test
        @DisplayName("Should calculate average consumption correctly filtering invalid records")
        void shouldCalculateAverageConsumptionCorrectly() {
            ServiceOrder validOrder = new ServiceOrder();
            validOrder.setDepartureKm(new BigDecimal("1000.00"));
            validOrder.setArrivalKm(new BigDecimal("1500.00")); // 500 km traveled

            Refueling validRefueling = new Refueling();
            validRefueling.setServiceOrder(validOrder);
            validRefueling.setLiters(new BigDecimal("50.00")); // 500 / 50 = 10 km/L

            Refueling invalidRefueling1 = new Refueling(); // missing service order

            Refueling invalidRefueling2 = new Refueling();
            invalidRefueling2.setServiceOrder(new ServiceOrder()); // missing departure/arrival km

            when(refuelingRepository.findByVehicleId(1L))
                    .thenReturn(List.of(validRefueling, invalidRefueling1, invalidRefueling2));

            Double average = vehicleService.calculateAverageConsumption(1L);

            assertEquals(10.0, average);
        }
    }

    @Nested
    @DisplayName("Tests for atualizarKmTrocaOleo")
    class AtualizarKmTrocaOleoTests {

        @Test
        @DisplayName("Should update oil change KM successfully")
        void shouldUpdateOilChangeKmSuccessfully() {
            BigDecimal newKmTroca = new BigDecimal("80000.00");

            when(repository.findById(1L)).thenReturn(Optional.of(mockVehicle));
            when(repository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

            VehicleResponseDTO result = vehicleService.atualizarKmTrocaOleo(1L, newKmTroca);

            assertNotNull(result);
            assertEquals(newKmTroca, mockVehicle.getKmTrocaOleo());
        }

        @Test
        @DisplayName("Should throw exception when vehicle not found for oil change KM update")
        void shouldThrowExceptionWhenVehicleNotFoundForOilChangeUpdate() {
            when(repository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> vehicleService.atualizarKmTrocaOleo(1L, new BigDecimal("80000.00")));

            assertEquals("Vehicle not found", exception.getMessage());
        }
    }
}