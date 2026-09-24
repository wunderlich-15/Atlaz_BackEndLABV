package com.example.AtlazDB.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

import com.example.AtlazDB.model.*;
import com.example.AtlazDB.repository.*;
import org.springframework.stereotype.Service;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.VehicleStatus;

@Service
public class VehicleService {

    private final VehicleRepository repository;
    private final ModelRepository modelRepository;
    private final ServiceOrderRepository serviceOrderRepository;
    private final UserRepository userRepository;
    private final RefuelingRepository refuelingRepository;

    public VehicleService(VehicleRepository repository, ModelRepository modelRepository, ServiceOrderRepository serviceOrderRepository, UserRepository userRepository, RefuelingRepository refuelingRepository) {
        this.repository = repository;
        this.modelRepository = modelRepository;
        this.serviceOrderRepository = serviceOrderRepository;
        this.userRepository = userRepository;
        this.refuelingRepository = refuelingRepository;
    }

    public List<VehicleResponseDTO> findAvailableVehiclesByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return repository.findAll()
                .stream()

                // somente viaturas disponiveis
                .filter(vehicle ->
                        vehicle.getVehicleStatus() == VehicleStatus.DISPONIVEL
                )

                // somente viaturas compativeis com a CNH
                .filter(vehicle ->
                        user.getCnhTypes()
                                .contains(vehicle.getTipoCnhNecessaria())
                )

                .map(VehicleResponseDTO::new)
                .toList();
    }

    public List<VehicleResponseDTO> listAll() {
        return repository.findAll()
                .stream()
                .map(VehicleResponseDTO::new)
                .toList();
    }

    public VehicleResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(VehicleResponseDTO::new)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public VehicleResponseDTO save(VehicleRequestDTO dto) {

        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new RuntimeException("Model not found!"));

        Vehicle vehicle = new Vehicle();

        vehicle.setPrefix(dto.getPrefix());
        vehicle.setType(dto.getType());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setTipoCnhNecessaria(
                dto.getTipoCnhNecessaria() != null
                        ? dto.getTipoCnhNecessaria()
                        : CnhType.B);
        vehicle.setModel(model);
        if (dto.getStatus() != null) {
            vehicle.setVehicleStatus(dto.getStatus());
        } else {
            vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        }

        double km = dto.getKm() != null ? dto.getKm() : 0.0;
        vehicle.setKm(km);

        // Se não foi informado, define o default como km atual + 10.000
        vehicle.setKmTrocaOleo(
                BigDecimal.valueOf(dto.getKmTrocaOleo() != null
                        ? dto.getKmTrocaOleo()
                        : km + 10000)
        );

        Vehicle saved = repository.save(vehicle);

        return new VehicleResponseDTO(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public VehicleResponseDTO update(Long id, VehicleRequestDTO dto) {

        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found."));

        Model model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new RuntimeException("Model not found!"));

        if (dto.getStatus() == VehicleStatus.DESATIVADA) {
            boolean hasActiveOrder = serviceOrderRepository.existsByVehicleIdAndReturnDateIsNull(id);
            if (hasActiveOrder) {
                throw new BusinessRuleException("Não é possível inativar a viatura porque ela está vinculada a uma Ordem de Serviço em andamento.");
            }
        }

        vehicle.setPrefix(dto.getPrefix());
        vehicle.setType(dto.getType());
        vehicle.setFuelType(dto.getFuelType());
        vehicle.setTipoCnhNecessaria(dto.getTipoCnhNecessaria());
        vehicle.setModel(model);

        if (dto.getStatus() != null) {
            vehicle.setVehicleStatus(dto.getStatus());
        }
        if (dto.getKm() != null) {
            vehicle.setKm(dto.getKm());
        }
        if (dto.getTipoCnhNecessaria() != null) {
            vehicle.setTipoCnhNecessaria(dto.getTipoCnhNecessaria());
        }

        Vehicle updated = repository.save(vehicle);

        return new VehicleResponseDTO(updated);
    }
    
    public void updateCurrentKm(Long vehicleId) {
        ServiceOrder last = serviceOrderRepository
                .findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(vehicleId);

        if (last != null && last.getArrivalKm() != null) {
            Vehicle vehicle = repository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            vehicle.setKm(last.getArrivalKm().doubleValue());
            repository.save(vehicle);
        }
    }


    public Double calculateAverageConsumption(Long id) {
        List<Refueling> abastecimentos = refuelingRepository.findByVehicleId(id);

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

        if (consumos.isEmpty()) return 0.0;

        double media = consumos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        return BigDecimal.valueOf(media).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public VehicleResponseDTO atualizarKmTrocaOleo(Long id, BigDecimal kmTrocaOleo) {
        Vehicle v = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        v.setKmTrocaOleo(kmTrocaOleo);
        return new VehicleResponseDTO(repository.save(v));
    }
}