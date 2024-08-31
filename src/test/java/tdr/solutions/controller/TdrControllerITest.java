package tdr.solutions.controller;

import tdr.solutions.model.TripDetailRecordEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class TdrControllerITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createTdrSuccessful() throws Exception {
        TripDetailRecordEntity tripDetailRecordEntity = new TripDetailRecordEntity();
        tripDetailRecordEntity.setVehicleId("vehicle-id1323");
        tripDetailRecordEntity.setTotalCost(20.0);
        tripDetailRecordEntity.setStartTime(LocalDateTime.parse("2023-11-24T14:15:00"));
        tripDetailRecordEntity.setEndTime(LocalDateTime.parse("2023-11-24T14:15:00").plusHours(3));
        tripDetailRecordEntity.setSessionId("unique-session11");

        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestPost("/tdr")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripDetailRecordEntity))).andExpect(status().isOk());

    }

    @Test
    void createSameCdrReturnException() throws Exception {
        TripDetailRecordEntity tripDetailRecordEntity = new TripDetailRecordEntity();
        tripDetailRecordEntity.setVehicleId("vehicle-id123");
        tripDetailRecordEntity.setTotalCost(20.0);
        tripDetailRecordEntity.setStartTime(LocalDateTime.parse("2023-11-24T14:15:00"));
        tripDetailRecordEntity.setEndTime(LocalDateTime.parse("2023-11-24T14:15:00").plusHours(3));
        tripDetailRecordEntity.setSessionId("unique-session11654");
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestPost("/tdr").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripDetailRecordEntity))).andExpect(status().isOk());

        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestPost("/tdr").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripDetailRecordEntity))).andExpect(status().is4xxClientError());

    }

    @Test
    void getTdrBySessionId() throws Exception {
        TripDetailRecordEntity tripDetailRecordEntity = new TripDetailRecordEntity();
        tripDetailRecordEntity.setVehicleId("vehicle-id45");
        tripDetailRecordEntity.setTotalCost(20.0);
        tripDetailRecordEntity.setStartTime(LocalDateTime.parse("2023-11-24T14:15:00"));
        tripDetailRecordEntity.setEndTime(LocalDateTime.parse("2023-11-24T14:15:00").plusHours(3));
        tripDetailRecordEntity.setSessionId("unique-session112");

        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestPost("/tdr").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripDetailRecordEntity))).andExpect(status().isOk());

        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/unique-session112")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("unique-session11")));

    }

    @Test
    void getTdrBySessionIdNotFound() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/notFoundSessionId")).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getTdrsByVehicleId() throws Exception {
        TripDetailRecordEntity tripDetailRecordEntity = new TripDetailRecordEntity();
        tripDetailRecordEntity.setVehicleId("vehicleId234");
        tripDetailRecordEntity.setTotalCost(20.0);
        tripDetailRecordEntity.setStartTime(LocalDateTime.parse("2023-11-24T14:15:00"));
        tripDetailRecordEntity.setEndTime(LocalDateTime.parse("2023-11-24T14:15:00").plusHours(3));
        tripDetailRecordEntity.setSessionId("unique-session11321");

        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestPost("/tdr").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tripDetailRecordEntity))).andExpect(status().isOk());

        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicleId234")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("vehicleId234")));

    }

    @Test
    void getTdrsByVehicleIdWithEmptyResult() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicleId234")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void testGetTdrsByVehicleIdAscOrder() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicle10")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sortBy", "startTime")
                    .param("sortOrder", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].vehicleId").value("vehicle10"))
                .andExpect(jsonPath("$[0].sessionId").value("session8"))
                .andExpect(jsonPath("$[1].sessionId").value("session11"))
                .andExpect(jsonPath("$[0].startTime").value("2023-11-23T10:00:00"))
                .andExpect(jsonPath("$[2].startTime").value("2023-11-26T13:00:00"))
                .andExpect(jsonPath("$[2].vehicleId").value("vehicle10"));
    }

    @Test
    void testGetTdrsByVehicleIdDescOrderByStartTime() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicle10")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sortBy", "startTime")
                    .param("sortOrder", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].vehicleId").value("vehicle10"))
                .andExpect(jsonPath("$[0].sessionId").value("session20"))
                .andExpect(jsonPath("$[1].sessionId").value("session18"))
                .andExpect(jsonPath("$[0].startTime").value("2024-11-29T08:30:00"))
                .andExpect(jsonPath("$[2].startTime").value("2024-11-26T13:00:00"))
                .andExpect(jsonPath("$[2].vehicleId").value("vehicle10"));
    }

    @Test
    void testGetTdrsByVehicleIdAscOrderByEndTime() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicle10")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sortBy", "endTime")
                    .param("sortOrder", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].vehicleId").value("vehicle10"))
                .andExpect(jsonPath("$[0].sessionId").value("session8"))
                .andExpect(jsonPath("$[1].sessionId").value("session11"))
                .andExpect(jsonPath("$[0].endTime").value("2023-11-23T11:15:00"))
                .andExpect(jsonPath("$[2].endTime").value("2023-11-26T14:30:00"))
                .andExpect(jsonPath("$[2].vehicleId").value("vehicle10"));
    }

    @Test
    void testGetTdrsByVehicleIdDescOrderByEndTime() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicle10")
                    .param("page", "0")
                    .param("size", "3")
                    .param("sortBy", "endTime")
                    .param("sortOrder", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].vehicleId").value("vehicle10"))
                .andExpect(jsonPath("$[0].sessionId").value("session20"))
                .andExpect(jsonPath("$[1].sessionId").value("session18"))
                .andExpect(jsonPath("$[0].endTime").value("2024-11-29T10:00:00"))
                .andExpect(jsonPath("$[2].endTime").value("2024-11-26T14:30:00"))
                .andExpect(jsonPath("$[2].vehicleId").value("vehicle10"));
    }

    @Test
    void testGetTdrsByVehicleIdDescOrderByStartTimeNextPage() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicle10")
                    .param("page", "1")
                    .param("size", "3")
                    .param("sortBy", "startTime")
                    .param("sortOrder", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].vehicleId").value("vehicle10"))
                .andExpect(jsonPath("$[0].sessionId").value("session11"))
                .andExpect(jsonPath("$[1].sessionId").value("session8"))
                .andExpect(jsonPath("$[0].startTime").value("2024-11-24T14:15:00"))
                .andExpect(jsonPath("$[2].startTime").value("2023-11-24T14:15:00"))
                .andExpect(jsonPath("$[2].vehicleId").value("vehicle10"));
    }

    @Test
    void testGetTdrsByVehicleIdDescOrderByEndTimeNextPage() throws Exception {
        this.mockMvc.perform(RequestFactoryTest.myFactoryRequestGet("/tdr/vehicle/vehicle10")
                    .param("page", "1")
                    .param("size", "3")
                    .param("sortBy", "endTime")
                    .param("sortOrder", "desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].vehicleId").value("vehicle10"))
                .andExpect(jsonPath("$[0].sessionId").value("session11"))
                .andExpect(jsonPath("$[1].sessionId").value("session8"))
                .andExpect(jsonPath("$[0].endTime").value("2024-11-24T16:00:00"))
                .andExpect(jsonPath("$[2].endTime").value("2023-11-24T16:00:00"))
                .andExpect(jsonPath("$[2].vehicleId").value("vehicle10"));
    }

}