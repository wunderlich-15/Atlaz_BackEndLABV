package com.example.AtlazDB.controller;

import com.example.AtlazDB.dto.ModelRequestDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.AtlazDB.model.Model;
import com.example.AtlazDB.service.ModelService;

@RestController
@RequestMapping("/models")
public class ModelController {

    private final ModelService service;

    public ModelController(ModelService service) {
        this.service = service;
    }

    @GetMapping
    public List<Model> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public Model findById(@PathVariable Long id) {
        return service.findById(id).orElse(null);
    }

    @PostMapping
    public Model create(@RequestBody ModelRequestDTO dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public Model update(@PathVariable Long id, @RequestBody ModelRequestDTO model) {
        return service.update(id,model);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}