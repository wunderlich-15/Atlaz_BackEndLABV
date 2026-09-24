package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.City;

public interface CityRepository extends JpaRepository<City, Long> {
}