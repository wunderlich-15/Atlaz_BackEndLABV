package com.example.AtlazDB.dto;

import com.example.AtlazDB.model.City;

public record CityResponseDTO(
        Long id,
        String nome,
        String uf
) {
    // Construtor para converter a Entidade em DTO
    public CityResponseDTO(City city) {
        this(
                city.getId(),
                city.getName(),
                city.getUf()
        );
    }
}