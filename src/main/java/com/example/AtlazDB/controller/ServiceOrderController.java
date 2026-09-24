package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ServiceOrderRequestDTO;
import com.example.AtlazDB.enums.OccurrenceType;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.service.RefuelingService;
import com.example.AtlazDB.service.GenerateCsv;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.service.ServiceOrderService;

@RestController
@RequestMapping("/service-orders")
public class ServiceOrderController {

    private final ServiceOrderService service;
    private final RefuelingService refuelingService;
    private final GenerateCsv generateCsv;

    public ServiceOrderController(ServiceOrderService service,
                                  RefuelingService refuelingService,
                                  GenerateCsv generateCsv) {
        this.service = service;
        this.refuelingService = refuelingService;
        this.generateCsv = generateCsv;
    }

    @GetMapping
    public List<ServiceOrder> list() {
        return service.listAll();
    }

    @GetMapping("/occurrence-types")
    public OccurrenceType[] listTypes() {
        return OccurrenceType.values();
    }

    @GetMapping("/{id}")
    public ServiceOrder findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ServiceOrder>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUserId(userId));
    }

    @PostMapping
    public ServiceOrder create(@RequestBody ServiceOrderRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ServiceOrder update(@PathVariable Long id, @RequestBody ServiceOrderRequestDTO dto) {
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


    @GetMapping("/csv")
    public ResponseEntity<byte[]> downloadCSV(
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String categoria) {

        List<ServiceOrder> orders;
        List<Refueling> refuelings;

        if (dataInicio != null && dataFim != null) {
            orders = service.findByInterval(dataInicio, dataFim);
            refuelings = refuelingService.findByInterval(dataInicio, dataFim);
        } else if (month != null && year != null) {
            orders = service.findByMonthAndYear(month, year);
            refuelings = refuelingService.findByMonthAndYear(month, year);
        } else {
            orders = service.listAll();
            refuelings = refuelingService.listAll();
        }

        if ("abastecimento".equals(categoria)) orders = new ArrayList<>();
        if ("os".equals(categoria)) refuelings = new ArrayList<>();

        byte[] csvBytes = generateCsv.generateCSV(orders, refuelings);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=records.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(csvBytes);
    }
    @GetMapping("/by-month")
    public ResponseEntity<List<ServiceOrder>> findByMonth(
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(service.findByMonthAndYear(month, year));
    }

    @GetMapping("/count-by-interval")
    public ResponseEntity<Long> countByInterval(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        
        Long total = service.countServiceOrdersByDateInterval(dataInicio, dataFim);
        
        return ResponseEntity.ok(total);
    }
}