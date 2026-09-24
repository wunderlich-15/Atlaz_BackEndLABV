package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.CityRequestDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.City;
import com.example.AtlazDB.service.CityService;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityService service;

    public CityController(CityService service) {
        this.service = service;
    }

    @GetMapping
    public List<City> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public City findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public City create(@RequestBody CityRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public City update(@PathVariable Long id, @RequestBody CityRequestDTO dto) {
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}