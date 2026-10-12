package com.studentportal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentportal.dto.StudentResultRequest;
import com.studentportal.model.StudentResult;
import com.studentportal.service.StudentResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentResultController.class)
class StudentResultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentResultService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/results - Should return all student results")
    void testGetAllResults() throws Exception {
        StudentResult student = new StudentResult(
                "STU-101", "Aarav Sharma", "Class 10th", "aarav@example.com",
                90, 85, 80, 95, 90
        );

        when(service.getAllResults()).thenReturn(List.of(student));

        mockMvc.perform(get("/api/results"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].rollNumber").value("STU-101"))
                .andExpect(jsonPath("$.data[0].studentName").value("Aarav Sharma"));
    }

    @Test
    @DisplayName("GET /api/results/{rollNumber} - Should return result when student exists")
    void testGetByRollNumber_Found() throws Exception {
        StudentResult student = new StudentResult(
                "STU-102", "Priya Patel", "Class 10th", "priya@example.com",
                85, 78, 82, 89, 76
        );

        when(service.getResultByRollNumber("STU-102")).thenReturn(Optional.of(student));

        mockMvc.perform(get("/api/results/STU-102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rollNumber").value("STU-102"))
                .andExpect(jsonPath("$.data.studentName").value("Priya Patel"));
    }

    @Test
    @DisplayName("GET /api/results/{rollNumber} - Should return 404 when student not found")
    void testGetByRollNumber_NotFound() throws Exception {
        when(service.getResultByRollNumber("UNKNOWN")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/results/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("POST /api/results - Should create result and return 201")
    void testCreateResult_Success() throws Exception {
        StudentResultRequest req = new StudentResultRequest(
                "STU-107", "Test Student", "Class 10th", "test@example.com",
                85, 80, 75, 90, 85
        );

        StudentResult created = new StudentResult(
                "STU-107", "Test Student", "Class 10th", "test@example.com",
                85, 80, 75, 90, 85
        );

        when(service.createResult(any(StudentResultRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rollNumber").value("STU-107"));
    }

    @Test
    @DisplayName("POST /api/results - Should return 400 when validation fails (missing rollNumber)")
    void testCreateResult_ValidationFailure() throws Exception {
        StudentResultRequest invalidReq = new StudentResultRequest(
                "", "", "Class 10", null,
                -5, 120, 50, 50, 50
        );

        mockMvc.perform(post("/api/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
