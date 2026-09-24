package com.example.AtlazDB.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {
}
