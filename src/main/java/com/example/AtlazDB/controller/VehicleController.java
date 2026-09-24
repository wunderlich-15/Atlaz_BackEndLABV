package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.VehicleRequestDTO;
import com.example.AtlazDB.dto.VehicleResponseDTO;
import com.example.AtlazDB.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.AtlazDB.enums.VehicleStatus;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/available/{userId}")
    public List<VehicleResponseDTO> listAvailableVehicles(
            @PathVariable Long userId
    ) {
        return service.findAvailableVehiclesByUser(userId);
    }

    @PostMapping
    public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleRequestDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable Long id, @RequestBody VehicleRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vehicle-status")
    public ResponseEntity<VehicleStatus[]> getVehicleStatuses() {
        return ResponseEntity.ok(VehicleStatus.values());
    }

    @GetMapping("/{id}/average-consumption")
    public ResponseEntity<Double> getAverageConsumption(@PathVariable Long id) {
        Double averageConsumption = service.calculateAverageConsumption(id);
        return ResponseEntity.ok(averageConsumption);
    }

    // VehicleController.java
    @PatchMapping("/{id}/km-troca-oleo")
    public ResponseEntity<VehicleResponseDTO> atualizarKmTrocaOleo(
            @PathVariable Long id,
            @RequestBody BigDecimal kmTrocaOleo) {
        return ResponseEntity.ok(service.atualizarKmTrocaOleo(id, kmTrocaOleo));
    }
}
