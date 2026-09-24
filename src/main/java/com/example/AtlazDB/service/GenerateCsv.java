package com.example.AtlazDB.service;

import com.example.AtlazDB.model.Refueling;
import com.example.AtlazDB.model.ServiceOrder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class GenerateCsv {

    private String escape(Object value) {
        if (value == null) return "";
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
    }

    public byte[] generateCSV(List<ServiceOrder> orders, List<Refueling> refuelings) {
        StringBuilder csv = new StringBuilder();
        csv.append("vehicle,service_type,justification,requester,destination,departure_km,arrival_km,departure_date,return_date,liters,total_value,receipt\n");

        for (ServiceOrder so : orders) {
            Refueling r = refuelings.stream()
                    .filter(rf -> rf.getServiceOrder() != null && rf.getServiceOrder().getId().equals(so.getId()))
                    .findFirst()
                    .orElse(null);

            appendRow(csv,
                    so.getVehicle().getPrefix(),
                    so.getServiceType(),
                    so.getJustification(),
                    so.getRequester(),
                    so.getDestinationLocation(),
                    so.getDepartureKm(),
                    so.getArrivalKm(),
                    so.getDepartureDate(),
                    so.getReturnDate(),
                    r != null ? r.getLiters() : null,
                    r != null ? r.getTotalValue() : null,
                    r != null ? r.getReceiptNumber() : null);
        }

        refuelings.stream()
                .filter(r -> r.getServiceOrder() == null)
                .forEach(r -> appendRow(csv,
                        r.getVehicle() != null ? r.getVehicle().getPrefix() : null,
                        "ABASTECIMENTO",
                        null, null, null, null, null,
                        r.getDateTime(),
                        null,
                        r.getLiters(),
                        r.getTotalValue(),
                        r.getReceiptNumber()));

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void appendRow(StringBuilder csv, Object... fields) {
        for (int i = 0; i < fields.length; i++) {
            csv.append(escape(fields[i]));
            csv.append(i < fields.length - 1 ? "," : "\n");
        }
    }
}