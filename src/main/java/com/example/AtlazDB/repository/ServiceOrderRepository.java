package com.example.AtlazDB.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.AtlazDB.model.ServiceOrder;
import com.example.AtlazDB.repository.projection.AtividadeProjection;

public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    @Query("SELECT o FROM ServiceOrder o WHERE o.departureDate >= :start AND o.departureDate < :end")
    List<ServiceOrder> findByPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<ServiceOrder> findByUser_IdOrderByDepartureDateDesc(Long userId);

    @Query(value = """
        SELECT
            os.id_os            AS id,
            'DESLOCAMENTO'      AS tipo,
            v.prefixo           AS prefixoViatura,
            u.nome              AS nomeTecnico,
            os.local_destino    AS descricao,
            CASE\s
                WHEN os.data_retorno IS NOT NULL THEN 'CONCLUÍDO'
                ELSE 'EM ANDAMENTO'
            END                 AS status,
            CASE\s
                WHEN os.data_retorno IS NOT NULL THEN 'EMERALD'
                ELSE 'AMBER'
            END                 AS corStatus
        FROM ordem_servico os
        JOIN viatura v  ON v.id_viatura = os.id_viatura
        JOIN usuario u  ON u.id_usuario = os.id_usuario
        WHERE DATE(os.data_saida) = CURRENT_DATE
       """, nativeQuery = true)
    List<AtividadeProjection> findOrdensDeHoje();

    ServiceOrder findTopByVehicle_IdAndReturnDateIsNotNullOrderByReturnDateDesc(Long viaturaId);

    long countByDepartureDateBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByUserIdAndReturnDateIsNull(Long userId);

    boolean existsByVehicleIdAndReturnDateIsNull(Long vehicleId);

}