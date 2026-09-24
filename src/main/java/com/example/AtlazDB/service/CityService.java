package com.example.AtlazDB.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.AtlazDB.dto.CityRequestDTO;
import com.example.AtlazDB.model.City;
import com.example.AtlazDB.repository.CityRepository;

@Service
public class CityService {

    private final CityRepository repository;

    public CityService(CityRepository repository) {
        this.repository = repository;
    }

    public List<City> listAll() {
        return repository.findAll();
    }

    public Optional<City> findById(Long id) {
        return repository.findById(id);
    }

    public City save(CityRequestDTO dto) {
        City city = new City();
        city.setName(dto.getName());
        city.setUf(dto.getUf());
        return repository.save(city);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public City update(Long id, CityRequestDTO dto) {
        City city = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found"));
        city.setName(dto.getName());
        city.setUf(dto.getUf());
        return repository.save(city);
    }
}