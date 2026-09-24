package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.model.Vehicle;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPrefix(String prefix);
    Integer countByVehicleStatus(VehicleStatus status);

}