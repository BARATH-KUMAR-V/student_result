package com.studentportal.controller;

import com.studentportal.dto.ApiResponse;
import com.studentportal.dto.StudentResultRequest;
import com.studentportal.dto.StudentStatsResponse;
import com.studentportal.model.StudentResult;
import com.studentportal.service.StudentResultService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*")
public class StudentResultController {

    private final StudentResultService service;

    @Autowired
    public StudentResultController(StudentResultService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResult>>> getAllResults(
            @RequestParam(required = false) String search) {
        List<StudentResult> results = (search != null && !search.trim().isEmpty())
                ? service.searchResults(search)
                : service.getAllResults();
        return ResponseEntity.ok(ApiResponse.ok("Results retrieved successfully", results));
    }

    @GetMapping("/{rollNumber}")
    public ResponseEntity<ApiResponse<StudentResult>> getByRollNumber(@PathVariable String rollNumber) {
        Optional<StudentResult> result = service.getResultByRollNumber(rollNumber);
        return result.map(studentResult -> ResponseEntity.ok(ApiResponse.ok("Result found", studentResult)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("No student record found with Roll Number: " + rollNumber)));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<StudentResult>> getById(@PathVariable Long id) {
        Optional<StudentResult> result = service.getResultById(id);
        return result.map(studentResult -> ResponseEntity.ok(ApiResponse.ok("Result found", studentResult)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Student record not found with ID: " + id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResult>> createResult(@Valid @RequestBody StudentResultRequest request) {
        try {
            StudentResult created = service.createResult(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Student result recorded successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResult>> updateResult(
            @PathVariable Long id,
            @Valid @RequestBody StudentResultRequest request) {
        try {
            StudentResult updated = service.updateResult(id, request);
            return ResponseEntity.ok(ApiResponse.ok("Student result updated successfully", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResult(@PathVariable Long id) {
        try {
            service.deleteResult(id);
            return ResponseEntity.ok(ApiResponse.ok("Student record deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StudentStatsResponse>> getStats() {
        StudentStatsResponse stats = service.getStatistics();
        return ResponseEntity.ok(ApiResponse.ok("Statistics retrieved successfully", stats));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Validation failed", errors));
    }
}
