package com.example.AtlazDB.model;

import com.example.AtlazDB.enums.OccurrenceType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico")
@Data
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_os")
    private Long id;

//    @Column(name = "service_order_number")
//    private String serviceOrderNumber;

    @Column(name = "tipo_servico")
    @Enumerated(EnumType.STRING)

    private OccurrenceType serviceType;

    @Column(name = "local_destino")
    private String destinationLocation;

    @Column(name = "justificativa")
    private String justification;

    @Column(name = "requisitante")
    private String requester;

    @Column(name = "km_saida")
    private BigDecimal departureKm;

    @Column(name = "km_chegada")
    private BigDecimal arrivalKm;

    @Column(name = "data_saida")
    private LocalDateTime departureDate;

    @Column(name = "data_retorno")
    private LocalDateTime returnDate;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_viatura")
    private Vehicle vehicle;

    public Long getId() {
        return id;
    }

    public OccurrenceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(OccurrenceType serviceType) {
        this.serviceType = serviceType;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public BigDecimal getDepartureKm() {
        return departureKm;
    }

    public void setDepartureKm(BigDecimal departureKm) {
        this.departureKm = departureKm;
    }

    public BigDecimal getArrivalKm() {
        return arrivalKm;
    }

    public void setArrivalKm(BigDecimal arrivalKm) {
        this.arrivalKm = arrivalKm;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
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
    
}