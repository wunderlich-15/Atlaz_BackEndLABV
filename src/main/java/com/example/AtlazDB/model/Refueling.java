package com.example.AtlazDB.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.AtlazDB.enums.*;
import lombok.Data;

@Entity
@Table(name = "abastecimento")
@Data
public class Refueling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abastecimento")
    private Long id;

    @Column(name = "data_hora")
    private LocalDateTime dateTime;

    @Column(name = "km_atual")
    private BigDecimal currentKm;

    @Column(name = "litros")
    private BigDecimal liters;

    @Column(name = "valor_total")
    private BigDecimal totalValue;

    @Column(name = "tipo_combustivel")
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;

    @Column(name = "numero_nota_fiscal")
    private String receiptNumber;

    @Column(name = "observacao_estado")
    private String stateObservation;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "id_cidade")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private City city;

    @ManyToOne
    @JoinColumn(name = "id_os")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ServiceOrder serviceOrder;

    public Long getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getCurrentKm() {
        return currentKm;
    }

    public void setCurrentKm(BigDecimal currentKm) {
        this.currentKm = currentKm;
    }

    public BigDecimal getLiters() {
        return liters;
    }

    public void setLiters(BigDecimal liters) {
        this.liters = liters;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getStateObservation() {
        return stateObservation;
    }

    public void setStateObservation(String stateObservation) {
        this.stateObservation = stateObservation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }
}