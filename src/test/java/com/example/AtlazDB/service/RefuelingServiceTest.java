package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.RefuelingRequestDTO;
import com.example.AtlazDB.enums.FuelType;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefuelingServiceTest {

    @Mock
    private RefuelingRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @InjectMocks
    private RefuelingService refuelingService;

    private User mockUser;
    private Vehicle mockVehicle;
    private City mockCity;
    private ServiceOrder mockServiceOrder;
    private Refueling mockRefueling;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);

        mockVehicle = new Vehicle();
        mockVehicle.setId(10L);

        mockCity = new City();
        mockCity.setId(100L);

        mockServiceOrder = new ServiceOrder();
        mockServiceOrder.setId(1000L);
        mockServiceOrder.setArrivalKm(new BigDecimal("50000.00"));

        mockRefueling = new Refueling();
        mockRefueling.setId(500L);
        mockRefueling.setTotalValue(new BigDecimal("250.00"));
        mockRefueling.setLiters(new BigDecimal("45.00"));
        mockRefueling.setUser(mockUser);
        mockRefueling.setVehicle(mockVehicle);
        mockRefueling.setCity(mockCity);
    }

    @Nested
    @DisplayName("Tests for Read Queries")
    class ReadQueriesTests {

        @Test
        @DisplayName("Should return all refuelings")
        void shouldReturnAllRefuelings() {
            when(repository.findAll()).thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.listAll();

            assertEquals(1, result.size());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should find refueling by ID")
        void shouldFindById() {
            when(repository.findById(500L)).thenReturn(Optional.of(mockRefueling));

            Optional<Refueling> result = refuelingService.findById(500L);

            assertTrue(result.isPresent());
            assertEquals(500L, result.get().getId());
        }

        @Test
        @DisplayName("Should find refuelings by month and year")
        void shouldFindByMonthAndYear() {
            when(repository.findByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.findByMonthAndYear(10, 2026);

            assertEquals(1, result.size());
            verify(repository, times(1)).findByPeriod(any(), any());
        }

        @Test
        @DisplayName("Should count refuelings by date interval")
        void shouldCountRefuelingsByDateInterval() {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 8);

            when(repository.countByDateTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(10L);

            Long count = refuelingService.countRefuelingsByDateInterval(start, end);

            assertEquals(10L, count);
        }

        @Test
        @DisplayName("Should find refuelings by date interval")
        void shouldFindByInterval() {
            LocalDate start = LocalDate.of(2026, 10, 1);
            LocalDate end = LocalDate.of(2026, 10, 8);

            when(repository.findByPeriod(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.findByInterval(start, end);

            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Tests for searchWithFilters")
    class FilterTests {

        @Test
        @DisplayName("Should filter by fuelType when fuelType is provided")
        void shouldFilterByFuelType() {
            when(repository.findByFuelType(FuelType.DIESEL)).thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.searchWithFilters(FuelType.DIESEL, 100.0, 5000.0);

            assertEquals(1, result.size());
            verify(repository, times(1)).findByFuelType(FuelType.DIESEL);
            verify(repository, never()).findByTotalValueGreaterThan(any());
        }

        @Test
        @DisplayName("Should filter by totalValue when fuelType is null and totalValue is provided")
        void shouldFilterByTotalValue() {
            when(repository.findByTotalValueGreaterThan(100.0)).thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.searchWithFilters(null, 100.0, 5000.0);

            assertEquals(1, result.size());
            verify(repository, times(1)).findByTotalValueGreaterThan(100.0);
            verify(repository, never()).findByCurrentKmGreaterThan(any());
        }

        @Test
        @DisplayName("Should filter by currentKm when fuelType and totalValue are null")
        void shouldFilterByCurrentKm() {
            when(repository.findByCurrentKmGreaterThan(5000.0)).thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.searchWithFilters(null, null, 5000.0);

            assertEquals(1, result.size());
            verify(repository, times(1)).findByCurrentKmGreaterThan(5000.0);
        }

        @Test
        @DisplayName("Should return all when all filter parameters are null")
        void shouldReturnAllWhenAllFiltersAreNull() {
            when(repository.findAll()).thenReturn(List.of(mockRefueling));

            List<Refueling> result = refuelingService.searchWithFilters(null, null, null);

            assertEquals(1, result.size());
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Tests for save")
    class SaveTests {

        @Test
        @DisplayName("Should throw exception when User is not found on save")
        void shouldThrowExceptionWhenUserNotFoundOnSave() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(99L);

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> refuelingService.save(dto));

            assertEquals("User not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when Vehicle is not found on save")
        void shouldThrowExceptionWhenVehicleNotFoundOnSave() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(99L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> refuelingService.save(dto));

            assertEquals("Vehicle not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when City is not found on save")
        void shouldThrowExceptionWhenCityNotFoundOnSave() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(99L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> refuelingService.save(dto));

            assertEquals("City not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when ServiceOrder is provided but not found on save")
        void shouldThrowExceptionWhenServiceOrderNotFoundOnSave() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(100L);
            dto.setServiceOrderId(999L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(100L)).thenReturn(Optional.of(mockCity));
            when(serviceOrderRepository.findById(999L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> refuelingService.save(dto));

            assertEquals("Service order not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should save refueling with ServiceOrder and arrival KM")
        void shouldSaveRefuelingWithServiceOrderAndArrivalKm() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(100L);
            dto.setServiceOrderId(1000L);
            dto.setTotalValue(new BigDecimal("200.00"));

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(100L)).thenReturn(Optional.of(mockCity));
            when(serviceOrderRepository.findById(1000L)).thenReturn(Optional.of(mockServiceOrder));
            when(repository.save(any(Refueling.class))).thenAnswer(i -> i.getArgument(0));

            Refueling result = refuelingService.save(dto);

            assertNotNull(result);
            assertEquals(new BigDecimal("50000.00"), result.getCurrentKm());
            verify(repository, times(1)).save(any(Refueling.class));
        }

        @Test
        @DisplayName("Should save refueling without ServiceOrder")
        void shouldSaveRefuelingWithoutServiceOrder() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(100L);
            dto.setServiceOrderId(null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(100L)).thenReturn(Optional.of(mockCity));
            when(repository.save(any(Refueling.class))).thenAnswer(i -> i.getArgument(0));

            Refueling result = refuelingService.save(dto);

            assertNotNull(result);
            assertNull(result.getServiceOrder());
            assertNull(result.getCurrentKm());
        }
    }

    @Nested
    @DisplayName("Tests for update and delete")
    class UpdateAndDeleteTests {

        @Test
        @DisplayName("Should delete refueling by ID")
        void shouldDeleteRefueling() {
            refuelingService.delete(500L);
            verify(repository, times(1)).deleteById(500L);
        }

        @Test
        @DisplayName("Should throw exception when Refueling is not found on update")
        void shouldThrowExceptionWhenRefuelingNotFoundOnUpdate() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(100L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(100L)).thenReturn(Optional.of(mockCity));
            when(repository.findById(500L)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> refuelingService.update(500L, dto));

            assertEquals("Refueling not found.", exception.getMessage());
        }

        @Test
        @DisplayName("Should update refueling successfully with ServiceOrder")
        void shouldUpdateRefuelingWithServiceOrder() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(100L);
            dto.setServiceOrderId(1000L);
            dto.setTotalValue(new BigDecimal("300.00"));

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(100L)).thenReturn(Optional.of(mockCity));
            when(repository.findById(500L)).thenReturn(Optional.of(mockRefueling));
            when(serviceOrderRepository.findById(1000L)).thenReturn(Optional.of(mockServiceOrder));
            when(repository.save(any(Refueling.class))).thenAnswer(i -> i.getArgument(0));

            Refueling result = refuelingService.update(500L, dto);

            assertNotNull(result);
            assertEquals(new BigDecimal("300.00"), result.getTotalValue());
            assertEquals(new BigDecimal("50000.00"), result.getCurrentKm());
        }

        @Test
        @DisplayName("Should update refueling successfully without ServiceOrder")
        void shouldUpdateRefuelingWithoutServiceOrder() {
            RefuelingRequestDTO dto = new RefuelingRequestDTO();
            dto.setUserId(1L);
            dto.setVehicleId(10L);
            dto.setCityId(100L);
            dto.setServiceOrderId(null);

            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
            when(vehicleRepository.findById(10L)).thenReturn(Optional.of(mockVehicle));
            when(cityRepository.findById(100L)).thenReturn(Optional.of(mockCity));
            when(repository.findById(500L)).thenReturn(Optional.of(mockRefueling));
            when(repository.save(any(Refueling.class))).thenAnswer(i -> i.getArgument(0));

            Refueling result = refuelingService.update(500L, dto);

            assertNotNull(result);
            assertNull(result.getServiceOrder());
            assertNull(result.getCurrentKm());
        }
    }
}