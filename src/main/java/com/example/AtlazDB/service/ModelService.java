package com.example.AtlazDB.service;

import com.example.AtlazDB.dto.ModelRequestDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.repository.ModelRepository;

@Service
public class ModelService {

    private final ModelRepository repository;

    public ModelService(ModelRepository repository) {
        this.repository = repository;
    }

    public List<Model> listAll() {
        return repository.findAll();
    }

    public Optional<Model> findById(Long id) {
        return repository.findById(id);
    }

    public Model save(ModelRequestDTO dto) {
        Model model = new Model();
        model.setModelName(dto.getNameModel());
        model.setBrandName(dto.getNameBrand());
        return repository.save(model);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Model update(Long id, ModelRequestDTO dto) {
        Model model = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Model not found"));
        model.setModelName(dto.getNameModel());
        model.setBrandName(dto.getNameBrand());
        return repository.save(model);
    }
}