package com.example.AtlazDB.dto;

import com.example.AtlazDB.model.Model;

public record ModelResponseDTO(
        Long id,
        String modelName,
        String brandName
) {
    // Constructor to convert the Entity into DTO
    public ModelResponseDTO(Model model) {
        this(
                model.getId(),
                model.getModelName(),
                model.getBrandName()
        );
    }
}