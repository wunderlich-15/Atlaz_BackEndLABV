package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.RefuelingRequestDTO;
import com.example.AtlazDB.enums.FuelType;
import com.example.AtlazDB.model.*;
import com.example.AtlazDB.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RefuelingService {

    private final RefuelingRepository repository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final CityRepository cityRepository;
    private final ServiceOrderRepository serviceOrderRepository;

    public RefuelingService(RefuelingRepository repository,
                            UserRepository userRepository,
                            VehicleRepository vehicleRepository,
                            CityRepository cityRepository,
                            ServiceOrderRepository serviceOrderRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.cityRepository = cityRepository;
        this.serviceOrderRepository = serviceOrderRepository;
    }

    public List<Refueling> listAll() {
        return repository.findAll();
    }

    public Optional<Refueling> findById(Long id) {
        return repository.findById(id);
    }

    public Refueling save(RefuelingRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found."));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found."));
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found."));

        // serviceOrder é opcional
        ServiceOrder serviceOrder = null;
        if (dto.getServiceOrderId() != null) {
            serviceOrder = serviceOrderRepository.findById(dto.getServiceOrderId())
                    .orElseThrow(() -> new RuntimeException("Service order not found."));
        }

        Refueling refueling = new Refueling();
        refueling.setTotalValue(dto.getTotalValue());
        refueling.setLiters(dto.getLiters());
        refueling.setDateTime(dto.getDateTime());
        refueling.setReceiptNumber(dto.getReceiptNumber());
        refueling.setUser(user);
        refueling.setVehicle(vehicle);
        refueling.setCity(city);
        refueling.setServiceOrder(serviceOrder);

        // currentKm só se tiver OS com chegada registrada
        if (serviceOrder != null && serviceOrder.getArrivalKm() != null) {
            refueling.setCurrentKm(serviceOrder.getArrivalKm());
        }

        return repository.save(refueling);
    }
    
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Refueling> searchWithFilters(
            FuelType fuelType,
            Double totalValue,
            Double currentKm
    ) {

        if (fuelType != null) {
            return repository.findByFuelType(fuelType);
        }

        if (totalValue != null) {
            return repository.findByTotalValueGreaterThan(totalValue);
        }

        if (currentKm != null) {
            return repository.findByCurrentKmGreaterThan(currentKm);
        }

        return repository.findAll();
    }

    public Refueling update(Long id, RefuelingRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found."));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found."));
        City city = cityRepository.findById(dto.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found."));
        Refueling refueling = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refueling not found."));
        ServiceOrder serviceOrder = null;
        if (dto.getServiceOrderId() != null) {
            serviceOrder = serviceOrderRepository.findById(dto.getServiceOrderId())
                    .orElseThrow(() -> new RuntimeException("Service order not found."));
        }

        refueling.setTotalValue(dto.getTotalValue());
        refueling.setCurrentKm(serviceOrder != null ? serviceOrder.getArrivalKm() : null);
        refueling.setLiters(dto.getLiters());
        refueling.setDateTime(dto.getDateTime());
        refueling.setReceiptNumber(dto.getReceiptNumber());
        refueling.setUser(user);
        refueling.setVehicle(vehicle);
        refueling.setCity(city);
        refueling.setServiceOrder(serviceOrder);

        if (serviceOrder != null && serviceOrder.getArrivalKm() != null) {
            refueling.setCurrentKm(serviceOrder.getArrivalKm());
        }

        return repository.save(refueling);
    }

    public List<Refueling> findByMonthAndYear(int month, int year) {

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);

        return repository.findByPeriod(start, end);
    }

    public Long countRefuelingsByDateInterval(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        
        LocalDateTime endOfDay = endDate.atTime(java.time.LocalTime.MAX);
        
        return repository.countByDateTimeBetween(startOfDay, endOfDay);
    }

    public List<Refueling> findByInterval(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime start = dataInicio.atStartOfDay();
        LocalDateTime end = dataFim.atTime(23, 59, 59);
        return repository.findByPeriod(start, end);
    }

}