package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ServiceOrderRequestDTO;
import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceOrderServiceTest {

    @Mock
    private ServiceOrderRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private ServiceOrderService serviceOrderService;

    private User mockUser;
    private Vehicle mockVehicle;
    private ServiceOrder mockServiceOrder;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setCnhTypes(Set.of(CnhType.B));
        mockUser.setUserStatus(UserStatus.DISPONIVEL);

        mockVehicle = new Vehicle();
        mockVehicle.setId(10L);
        mockVehicle.setPrefix("ABC-1234");
        mockVehicle.setTipoCnhNecessaria(CnhType.B);
        mockVehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        mockVehicle.setKm(50000.0);

        mockServiceOrder = new ServiceOrder();
        mockServiceOrder.setId(100L);
        mockServiceOrder.setUser(mockUser);
        mockServiceOrder.setVehicle(mockVehicle);
        mockServiceOrder.setServiceType(OccurrenceType.FISCALIZACAO);
        mockServiceOrder.setDepartureKm(new BigDecimal("50000.0"));
        mockServiceOrder.setDepartureDate(LocalDateTime.now().minusDays(1));
    }

    @Nested
    @DisplayName("Tests for Read Queries")
    class QueryTests {

        @Test
        @DisplayName("Should return all service orders")
        void shouldReturnAllServiceOrders() {
            when(repository.findAll()).thenReturn(List.of(mockServiceOrder));

            List<ServiceOrder> result = serviceOrderService.listAll();

            assertEquals(1, result.size());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should find service order by ID")
        void shouldFindById() {
            when(repository.findById(100L)).thenReturn(Optional.of(mockServiceOrder));

            Optional<ServiceOrder> result = serviceOrderService.findById(100L);

            assertTrue(result.isPresent());
            assertEquals(100L, result.get().getId());
        }

        @Test
        @DisplayName("Should find service orders by User ID")
        void shouldFindByUserId() {
            when(repository.findByUser_IdOrderByDepartureDateDesc(1L)).thenReturn(List.of(mockServiceOrder));

            List<ServiceOrder> result = serviceOrderService.findByUserId(1L);

            assertEquals(1, result.size());
            verify(repository, times(1)).findByUser_IdOrderByDepartureDateDesc(1L);
        }

        @Test
        @DisplayName("Should find service orders by Month and Year")
        void shouldFindByMonthAndYear() {
            when(repository.findByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(mockServiceOrder));

            List<ServiceOrder> result = serviceOrderService.findByMonthAndYear(10, 2026);

            assertEquals(1, result.size());
            verify(repository, times(1)).findByPeriod(any(), any());
        }

        @Test
        @DisplayName("Should count service orders by date interval")
        void shouldCountServiceOrdersByDateInterval() {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 8);

            when(repository.countByDepartureDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(5L);

            Long count = serviceOrderService.countServiceOrdersByDateInterval(start, end);

            assertEquals(5L, count);
        }

        @Test
        @DisplayName("Should find service orders by date interval")
        void shouldFindByInterval() {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 8);

            when(repository.findByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(mockServiceOrder));

            List<ServiceOrder> result = serviceOrderService.findByInterval(start, end);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Tests for save")
    class SaveTests {

        @Test
        @DisplayName("Should throw exception when User is not found")
        void shouldThrowExceptionWhenUserNotFound() {
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setUserId(99L);

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.save(dto));

            assertEquals("User not found!", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when Vehicle is not found")
        void shouldThrowExceptionWhenVehicleNotFound() {
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(99L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.save(dto));

            assertEquals("Vehicle not found!", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when User lacks required CNH")
        void shouldThrowExceptionWhenUserLacksRequiredCnh() {
            mockUser.setCnhTypes(Set.of(CnhType.A)); // User has A, Vehicle needs B

            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.save(dto));

            assertEquals("User does not have the required CNH for this vehicle.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when arrival KM is lower than last recorded KM")
        void shouldThrowExceptionWhenArrivalKmIsLowerThanLastRecorded() {
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setArrivalKm(new BigDecimal("40000.0")); // Lower than 50000.0

            ServiceOrder lastOrder = new ServiceOrder();
            lastOrder.setArrivalKm(new BigDecimal("50000.0"));

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(10L))
                    .thenReturn(lastOrder);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.save(dto));

            assertTrue(exception.getMessage().contains("Km cannot be less than the last recorded"));
        }

        @Test
        @DisplayName("Should save ServiceOrder and update statuses successfully")
        void shouldSaveServiceOrderSuccessfully() {
            LocalDateTime returnTime = LocalDateTime.now();
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setServiceType(OccurrenceType.FISCALIZACAO);
            dto.setArrivalKm(new BigDecimal("51000.0"));
            dto.setReturnDate(returnTime);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(repository.findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(10L)).thenReturn(null);
            when(repository.save(any(ServiceOrder.class))).thenAnswer(i -> {
                ServiceOrder so = i.getArgument(0);
                so.setId(100L);
                return so;
            });

            ServiceOrder result = serviceOrderService.save(dto);

            assertNotNull(result);
            assertEquals(VehicleStatus.EM_USO, mockVehicle.getVehicleStatus());
            assertEquals(UserStatus.EM_CAMPO, mockUser.getUserStatus());
            assertEquals(new BigDecimal("51000.0"), result.getArrivalKm());
            verify(vehicleService, times(1)).updateCurrentKm(10L);
        }
    }

    @Nested
    @DisplayName("Tests for update and delete")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Should delete service order by ID")
        void shouldDeleteServiceOrder() {
            serviceOrderService.delete(100L);
            verify(repository, times(1)).deleteById(100L);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing service order")
        void shouldThrowExceptionWhenUpdatingNonExistingOrder() {
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            when(repository.findById(100L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.update(100L, dto));

            assertEquals("Service order not found!", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when attempting to reduce vehicle KM during update")
        void shouldThrowExceptionWhenReducingKmOnUpdate() {
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setArrivalKm(new BigDecimal("40000.0")); // Lower than 50000.0

            when(repository.findById(100L)).thenReturn(Optional.of(mockServiceOrder));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.update(100L, dto));

            assertTrue(exception.getMessage().contains("Cannot reduce vehicle km"));
        }

        @Test
        @DisplayName("Should handle null current vehicle KM safely during reduction check")
        void shouldHandleNullCurrentVehicleKmOnUpdateCheck() {
            mockVehicle.setKm(null); // Vehicle KM is null
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setArrivalKm(new BigDecimal("-10.0")); // Lower than fallback 0.0

            when(repository.findById(100L)).thenReturn(Optional.of(mockServiceOrder));

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> serviceOrderService.update(100L, dto));

            assertTrue(exception.getMessage().contains("Current value: 0.0"));
        }

        @Test
        @DisplayName("Should update service order and release vehicle and user successfully")
        void shouldUpdateServiceOrderSuccessfully() {
            LocalDateTime returnTime = LocalDateTime.now();
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setArrivalKm(new BigDecimal("52000.0"));
            dto.setReturnDate(returnTime);

            when(repository.findById(100L)).thenReturn(Optional.of(mockServiceOrder));
            when(repository.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArgument(0));

            ServiceOrder result = serviceOrderService.update(100L, dto);

            assertNotNull(result);
            assertEquals(new BigDecimal("52000.0"), result.getArrivalKm());
            assertEquals(returnTime, result.getReturnDate());
            assertEquals(VehicleStatus.DISPONIVEL, mockVehicle.getVehicleStatus());
            assertEquals(UserStatus.DISPONIVEL, mockUser.getUserStatus());
            verify(vehicleService, times(2)).updateCurrentKm(10L);
        }

        @Test
        @DisplayName("Should update service order without changing arrival fields when DTO fields are null")
        void shouldUpdateServiceOrderWithoutModifyingArrivalWhenDtoFieldsAreNull() {
            ServiceOrderRequestDTO dto = new ServiceOrderRequestDTO();
            dto.setArrivalKm(null);
            dto.setReturnDate(null);

            when(repository.findById(100L)).thenReturn(Optional.of(mockServiceOrder));
            when(repository.save(any(ServiceOrder.class))).thenAnswer(i -> i.getArgument(0));

            ServiceOrder result = serviceOrderService.update(100L, dto);

            assertNotNull(result);
            assertNull(result.getArrivalKm());
            assertEquals(VehicleStatus.DISPONIVEL, mockVehicle.getVehicleStatus());
            assertEquals(UserStatus.DISPONIVEL, mockUser.getUserStatus());
        }
    }
}