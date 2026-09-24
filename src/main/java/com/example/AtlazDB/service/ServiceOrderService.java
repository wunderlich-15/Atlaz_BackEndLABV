package com.example.AtlazDB.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.AtlazDB.dto.ServiceOrderRequestDTO;
import com.example.AtlazDB.enums.UserStatus;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.model.User;
import com.example.AtlazDB.model.Vehicle;
import com.example.AtlazDB.repository.ServiceOrderRepository;
import com.example.AtlazDB.repository.UserRepository;
import com.example.AtlazDB.repository.VehicleRepository;

import java.time.LocalDate;

@Service
public class ServiceOrderService {

    private final ServiceOrderRepository repository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;

    public ServiceOrderService(ServiceOrderRepository repository,
                               UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               VehicleService vehicleService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehicleService = vehicleService;
    }

    public List<ServiceOrder> listAll() {
        return repository.findAll();
    }

    public Optional<ServiceOrder> findById(Long id) {
        return repository.findById(id);
    }

    public List<ServiceOrder> findByUserId(Long userId) {
        return repository.findByUser_IdOrderByDepartureDateDesc(userId);
    }

    public List<ServiceOrder> findByMonthAndYear(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1);
        return repository.findByPeriod(start, end);
    }

    public ServiceOrder save(ServiceOrderRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));

        if (!user.getCnhTypes().contains(vehicle.getTipoCnhNecessaria())) {
            throw new RuntimeException(
                    "User does not have the required CNH for this vehicle."
            );
        }

        ServiceOrder so = new ServiceOrder();
        so.setServiceType(dto.getServiceType());
        so.setDestinationLocation(dto.getDestinationLocation());
        so.setJustification(dto.getJustification());
        so.setRequester(dto.getRequester());
        so.setDepartureKm(dto.getDepartureKm());
        so.setDepartureDate(dto.getDepartureDate());
        so.setReturnDate(dto.getReturnDate());
        so.setUser(user);
        so.setVehicle(vehicle);

        ServiceOrder last = repository
                .findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(dto.getVehicleId());

        if (last != null && dto.getArrivalKm() != null) {
            double lastKm = last.getArrivalKm().doubleValue();
            if (dto.getArrivalKm().doubleValue() < lastKm) {
                throw new RuntimeException(
                    "Km cannot be less than the last recorded (" + lastKm + ")"
                );
            }
        }

        if (dto.getArrivalKm() != null && dto.getReturnDate() != null) {
            so.setArrivalKm(dto.getArrivalKm());
        }

        ServiceOrder saved = repository.save(so);

        vehicle.setVehicleStatus(VehicleStatus.EM_USO);
        vehicleRepository.save(vehicle);

        user.setUserStatus(UserStatus.EM_CAMPO);
        userRepository.save(user);

        if (saved.getArrivalKm() != null) {
            vehicleService.updateCurrentKm(saved.getVehicle().getId());
        }

        return saved;
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public ServiceOrder update(Long id, ServiceOrderRequestDTO dto) {
        ServiceOrder serviceOrder = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service order not found!"));

        // garante que km não diminui
        if (dto.getArrivalKm() != null) {
            double currentKm = serviceOrder.getVehicle().getKm() != null 
                ? serviceOrder.getVehicle().getKm() : 0.0;
            if (dto.getArrivalKm().doubleValue() < currentKm) {
                throw new RuntimeException(
                    "Cannot reduce vehicle km. Current value: " + currentKm
                );
            }
        }

        // atualiza só os campos de chegada
        if (dto.getArrivalKm() != null && dto.getReturnDate() != null) {
            serviceOrder.setArrivalKm(dto.getArrivalKm());
            serviceOrder.setReturnDate(dto.getReturnDate());
        }

        ServiceOrder updated = repository.save(serviceOrder);

        Vehicle vehicle = serviceOrder.getVehicle();
        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicleRepository.save(vehicle);

        User user = serviceOrder.getUser();
        user.setUserStatus(UserStatus.DISPONIVEL);
        userRepository.save(user);

        vehicleService.updateCurrentKm(vehicle.getId());

        // atualiza km da viatura
        vehicleService.updateCurrentKm(updated.getVehicle().getId());

        return updated;
    }
    
    public Long countServiceOrdersByDateInterval(LocalDate dataInicio, LocalDate dataFim) {
        // Pega do primeiro segundo do dia de início até o último segundo do dia de fim
        LocalDateTime startOfDay = dataInicio.atStartOfDay();
        LocalDateTime endOfDay = dataFim.atTime(java.time.LocalTime.MAX);
        
        // Conta os registros na tabela ordem_servico
        return repository.countByDepartureDateBetween(startOfDay, endOfDay);
    }

    public List<ServiceOrder> findByInterval(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime start = dataInicio.atStartOfDay();
        LocalDateTime end = dataFim.atTime(23, 59, 59);
        return repository.findByPeriod(start, end);
    }
}