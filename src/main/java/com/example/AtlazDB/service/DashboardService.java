package com.example.AtlazDB.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.AtlazDB.dto.DashboardResponseDTO;
import com.example.AtlazDB.enums.Profile;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.repository.RefuelingRepository;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.repository.VehicleRepository;
import com.example.AtlazDB.repository.projection.AtividadeProjection;

@Service
public class DashboardService {
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final RefuelingRepository refuelingRepository;

    public DashboardService(
            VehicleRepository vehicleRepository,
            UserRepository userRepository,
            ServiceOrderRepository serviceOrderRepository,
            RefuelingRepository refuelingRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.refuelingRepository = refuelingRepository;
    }

    public DashboardResponseDTO getDashboard() {
        return new DashboardResponseDTO(
                new DashboardResponseDTO.DataContainer(
                        buildMetrics(),
                        buildVehicleKm(),
                        buildTodayActivities()
                )
        );
    }

    private DashboardResponseDTO.Metrics buildMetrics() {
        return new DashboardResponseDTO.Metrics(
                (int) vehicleRepository.countByVehicleStatus(VehicleStatus.EM_USO),
                (int) vehicleRepository.countByVehicleStatus(VehicleStatus.DISPONIVEL),
                (int) userRepository.countByProfileAndUserStatus(Profile.TECNICO, UserStatus.EM_CAMPO),
                calcularConsumoMedio(),
                calcularGastoHoje()
        );
    }

    private BigDecimal calcularConsumoMedio() {
        List<Refueling> abastecimentos = refuelingRepository.findAll();

        List<Double> consumos = abastecimentos.stream()
                .filter(a -> a.getServiceOrder() != null
                        && a.getServiceOrder().getArrivalKm() != null
                        && a.getServiceOrder().getDepartureKm() != null
                        && a.getLiters() != null
                        && a.getLiters().doubleValue() > 0)
                .map(a -> {
                    double km = a.getServiceOrder().getArrivalKm().doubleValue()
                            - a.getServiceOrder().getDepartureKm().doubleValue();
                    double liters = a.getLiters().doubleValue();
                    return km / liters;
                })
                .filter(c -> c > 0)
                .toList();

        if (consumos.isEmpty()) return BigDecimal.ZERO;

        double media = consumos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        return BigDecimal.valueOf(media).setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularGastoHoje() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    private List<DashboardResponseDTO.VehicleKm> buildVehicleKm() {
        return new ArrayList<>(); // Lista reativa vazia aguardando queries futuras
    }

    private List<DashboardResponseDTO.TodayActivity> buildTodayActivities() {
        List<DashboardResponseDTO.TodayActivity> atividades = new ArrayList<>();

        serviceOrderRepository.findOrdensDeHoje()
                .stream()
                .map(this::toTodayActivity)
                .forEach(atividades::add);

        refuelingRepository.findAbastecimentosDeHoje()
                .stream()
                .map(this::toTodayActivity)
                .forEach(atividades::add);

        return atividades;
    }

    private DashboardResponseDTO.TodayActivity toTodayActivity(AtividadeProjection p) {
        return new DashboardResponseDTO.TodayActivity(
                p.getId(),
                p.getTipo(),
                p.getPrefixoViatura(),
                p.getNomeTecnico(),
                p.getDescricao(),
                p.getStatus(),
                p.getCorStatus()
        );
    }
}