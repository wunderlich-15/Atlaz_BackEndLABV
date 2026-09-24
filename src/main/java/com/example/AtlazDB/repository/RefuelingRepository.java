package com.example.AtlazDB.repository;

import com.example.AtlazDB.enums.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.repository.projection.AtividadeProjection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RefuelingRepository extends JpaRepository<Refueling, Long> {

    List<Refueling> findByFuelType(FuelType fuelType);

    List<Refueling> findByTotalValueGreaterThan(Double totalValue);

    List<Refueling> findByCurrentKmGreaterThan(Double currentKm);

    List<Refueling> findByVehicleId(Long vehicleId);

    @Query("SELECT r FROM Refueling r WHERE r.dateTime >= :start AND r.dateTime < :end")
    List<Refueling> findByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

     @Query(value = """
        SELECT 
            a.id_abastecimento                          AS id,
            'ABASTECIMENTO'                             AS tipo,
            v.prefixo                                   AS prefixoViatura,
            u.nome                                      AS nomeTecnico,
            CONCAT('Abastecimento: R$ ', a.valor_total) AS descricao,
            'CONCLUÍDO'                                 AS status,
            'EMERALD'                                   AS corStatus
        FROM abastecimento a
        JOIN viatura v  ON v.id_viatura = a.id_viatura
        JOIN usuario u  ON u.id_usuario = a.id_usuario
        WHERE DATE(a.data_hora) = CURRENT_DATE
        """, nativeQuery = true)
    List<AtividadeProjection> findAbastecimentosDeHoje();

    long countByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}