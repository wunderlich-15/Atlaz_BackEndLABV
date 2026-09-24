package com.example.AtlazDB;

import com.example.AtlazDB.enums.*;
import com.example.AtlazDB.model.*;
import com.example.AtlazDB.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RefuelingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ModelRepository modelRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private CityRepository cityRepository;
    @Autowired private ServiceOrderRepository serviceOrderRepository;
    @Autowired private RefuelingRepository refuelingRepository;

    private Long userId;
    private Long vehicleId;
    private Long cityId;
    private Long serviceOrderId;

    @BeforeEach
    void setup() {
        refuelingRepository.deleteAll();
        serviceOrderRepository.deleteAll();
        cityRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();
        modelRepository.deleteAll();

        User user = new User();
        user.setName("Test");
        user.setRegistration("000001");
        user.setEmail("test@email.com");
        user.setPasswordHash("password");
        user.setProfile(Profile.ADMIN);
        user.setUserStatus(UserStatus.DISPONIVEL);
        userId = userRepository.save(user).getId();

        Model model = new Model();
        model.setModelName("Prisma");
        model.setBrandName("Chevrolet");
        Model savedModel = modelRepository.save(model);

        Vehicle vehicle = new Vehicle();
        vehicle.setPrefix("US01");
        vehicle.setType(VehicleType.UTILITARIO);
        vehicle.setVehicleStatus(VehicleStatus.DISPONIVEL);
        vehicle.setModel(savedModel);
        vehicle.setFuelType(FuelType.GASOLINA); 
        vehicleId = vehicleRepository.save(vehicle).getId();

        City city = new City();
        city.setName("São José dos Campos");
        city.setUf("SP");
        cityId = cityRepository.save(city).getId();

        ServiceOrder so = new ServiceOrder();
        so.setServiceType(OccurrenceType.ADMINISTRATIVO);
        so.setDestinationLocation("Taubaté");
        so.setRequester("Test");
        so.setDepartureKm(new BigDecimal("100"));
        so.setDepartureDate(LocalDateTime.now());
        so.setUser(userRepository.findById(userId).get());
        so.setVehicle(vehicleRepository.findById(vehicleId).get());
        serviceOrderId = serviceOrderRepository.save(so).getId();
    }

    @Test
    void shouldCreateRefueling() throws Exception {
        mockMvc.perform(post("/refuelings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "dateTime": "2026-04-02T17:36:17",
                            "liters": 1,
                            "totalValue": 1,
                            "receiptNumber": "test",
                            "userId": %d,
                            "vehicleId": %d,
                            "cityId": %d,
                            "serviceOrderId": %d
                        }
                        """.formatted(userId, vehicleId, cityId, serviceOrderId)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindRefuelingsByMonth() throws Exception {
        mockMvc.perform(post("/refuelings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
            {
                "dateTime": "2026-04-02T17:36:17",
                "liters": 1,
                "totalValue": 1,
                "receiptNumber": "test",
                "userId": %d,
                "vehicleId": %d,
                "cityId": %d,
                "serviceOrderId": %d
            }
            """.formatted(userId, vehicleId, cityId, serviceOrderId)));

        mockMvc.perform(get("/refuelings/by-month")
                        .param("month", "4")
                        .param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnEmptyListForMonthWithoutRefueling() throws Exception {
        mockMvc.perform(get("/refuelings/by-month")
                        .param("month", "1")
                        .param("year", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldListOccurrenceTypes() throws Exception {
        mockMvc.perform(get("/service-orders/occurrence-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("ADMINISTRATIVO"));
    }

    @Test
    void shouldListVehicles() throws Exception {
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].prefix").value("US01"));
    }

    @Test
    void shouldGenerateCompleteCsv() throws Exception {
        mockMvc.perform(get("/service-orders/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(header().exists("Content-Disposition"));
    }

    @Test
    void shouldGenerateFilteredCsvByMonth() throws Exception {
        mockMvc.perform(get("/service-orders/csv")
                        .param("month", "4")
                        .param("year", "2026"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"));
    }
}