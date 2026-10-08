package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.DashboardResponseDTO;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.repository.RefuelingRepository;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import com.example.AtlazDB.repository.projection.AtividadeProjection;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceOrderRepository serviceOrderRepository;

    @Mock
    private RefuelingRepository refuelingRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Nested
    @DisplayName("Tests for getDashboard")
    class GetDashboardTests {

        @Test
        @DisplayName("Should assemble dashboard correctly with valid metrics and activities")
        void shouldAssembleDashboardWithValidMetricsAndActivities() {
            // Arrange
            when(vehicleRepository.countByVehicleStatus(VehicleStatus.EM_USO)).thenReturn(3);
            when(vehicleRepository.countByVehicleStatus(VehicleStatus.DISPONIVEL)).thenReturn(7);
            when(userRepository.countByProfileAndUserStatus(Profile.TECNICO, UserStatus.EM_CAMPO)).thenReturn(4L);

            // Refueling setup for average consumption calculation
            ServiceOrder validOrder = new ServiceOrder();
            validOrder.setDepartureKm(new BigDecimal("1000.0"));
            validOrder.setArrivalKm(new BigDecimal("1500.0")); // 500 km

            Refueling validRefueling = new Refueling();
            validRefueling.setServiceOrder(validOrder);
            validRefueling.setLiters(new BigDecimal("50.0")); // 500 / 50 = 10.0 km/L

            Refueling invalidRefuelingNullOrder = new Refueling();

            Refueling invalidRefuelingZeroLiters = new Refueling();
            invalidRefuelingZeroLiters.setServiceOrder(validOrder);
            invalidRefuelingZeroLiters.setLiters(BigDecimal.ZERO);

            when(refuelingRepository.findAll())
                    .thenReturn(List.of(validRefueling, invalidRefuelingNullOrder, invalidRefuelingZeroLiters));

            // Projections setup for today's activities
            AtividadeProjection orderProjection = mock(AtividadeProjection.class);
            when(orderProjection.getId()).thenReturn(101L);
            when(orderProjection.getTipo()).thenReturn("ORDEM_SERVICO");
            when(orderProjection.getPrefixoViatura()).thenReturn("ABC-1234");
            when(orderProjection.getNomeTecnico()).thenReturn("John Doe");
            when(orderProjection.getDescricao()).thenReturn("Routine check");
            when(orderProjection.getStatus()).thenReturn("EM_ANDAMENTO");
            when(orderProjection.getCorStatus()).thenReturn("#FF0000");

            AtividadeProjection refuelingProjection = mock(AtividadeProjection.class);
            when(refuelingProjection.getId()).thenReturn(202L);
            when(refuelingProjection.getTipo()).thenReturn("ABASTECIMENTO");
            when(refuelingProjection.getPrefixoViatura()).thenReturn("XYZ-9999");
            when(refuelingProjection.getNomeTecnico()).thenReturn("Jane Doe");
            when(refuelingProjection.getDescricao()).thenReturn("Full tank");
            when(refuelingProjection.getStatus()).thenReturn("CONCLUIDO");
            when(refuelingProjection.getCorStatus()).thenReturn("#00FF00");

            when(serviceOrderRepository.findOrdensDeHoje()).thenReturn(List.of(orderProjection));
            when(refuelingRepository.findAbastecimentosDeHoje()).thenReturn(List.of(refuelingProjection));

            // Act
            DashboardResponseDTO result = dashboardService.getDashboard();

            // Assert
            assertNotNull(result);
            assertNotNull(result.data());

            // Validate metrics
            DashboardResponseDTO.Metrics metrics = result.data().metrics();
            assertEquals(3, metrics.activeVehicles());
            assertEquals(7, metrics.totalVehicles());
            assertEquals(4, metrics.techniciansInField());
            assertEquals(new BigDecimal("10.0"), metrics.averageConsum());
            assertEquals(new BigDecimal("0.00"), metrics.todayExpense());

            // Validate vehicle KM
            assertNotNull(result.data().vehicleKm());
            assertTrue(result.data().vehicleKm().isEmpty());

            // Validate today's activities
            List<DashboardResponseDTO.TodayActivity> activities = result.data().todayActivities();
            assertEquals(2, activities.size());

            DashboardResponseDTO.TodayActivity activity1 = activities.get(0);
            assertEquals(101L, activity1.id());
            assertEquals("ORDEM_SERVICO", activity1.type());
            assertEquals("ABC-1234", activity1.vehiclePrefix());

            DashboardResponseDTO.TodayActivity activity2 = activities.get(1);
            assertEquals(202L, activity2.id());
            assertEquals("ABASTECIMENTO", activity2.type());
            assertEquals("XYZ-9999", activity2.vehiclePrefix());
        }

        @Test
        @DisplayName("Should return zero average consumption when refueling list is empty")
        void shouldReturnZeroAverageConsumptionWhenRefuelingsEmpty() {
            // Arrange
            when(vehicleRepository.countByVehicleStatus(VehicleStatus.EM_USO)).thenReturn(0);
            when(vehicleRepository.countByVehicleStatus(VehicleStatus.DISPONIVEL)).thenReturn(0);
            when(userRepository.countByProfileAndUserStatus(Profile.TECNICO, UserStatus.EM_CAMPO)).thenReturn(0L);
            when(refuelingRepository.findAll()).thenReturn(Collections.emptyList());
            when(serviceOrderRepository.findOrdensDeHoje()).thenReturn(Collections.emptyList());
            when(refuelingRepository.findAbastecimentosDeHoje()).thenReturn(Collections.emptyList());

            // Act
            DashboardResponseDTO result = dashboardService.getDashboard();

            // Assert
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.data().metrics().averageConsum());
            assertTrue(result.data().todayActivities().isEmpty());
        }
    }
}