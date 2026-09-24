package com.example.AtlazDB.model;

import jakarta.persistence.*;
import com.example.AtlazDB.enums.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "viatura")
@Data
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viatura")
    private Long id;

    @Column(name = "prefixo")
    private String prefix;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Column(name = "viatura_status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    @ManyToOne
    @JoinColumn(name = "id_modelo")
    private Model model;
    
    @Column(name = "tipo_abastecimento", nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "km_atual")
    private Double km;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cnh_necessaria")
    private CnhType tipoCnhNecessaria;

    @Column(name = "km_troca_oleo")
    private BigDecimal kmTrocaOleo;

}