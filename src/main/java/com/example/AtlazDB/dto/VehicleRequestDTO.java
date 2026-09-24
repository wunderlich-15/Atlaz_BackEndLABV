package com.example.AtlazDB.dto;

import com.example.AtlazDB.enums.CnhType;
import com.example.AtlazDB.enums.FuelType;
import com.example.AtlazDB.enums.VehicleStatus;
import com.example.AtlazDB.enums.VehicleType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleRequestDTO {

    private String prefix;
    private VehicleType type;
    private Long modelId;
    private FuelType fuelType;
    private Double km;
    private VehicleStatus status;
    private CnhType tipoCnhNecessaria;
    private Double kmTrocaOleo;
    

}