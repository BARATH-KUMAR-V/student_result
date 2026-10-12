package com.studentportal.service;

import com.studentportal.dto.StudentResultRequest;
import com.studentportal.dto.StudentStatsResponse;
import com.studentportal.model.StudentResult;
import com.studentportal.repository.StudentResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentResultServiceTest {

    @Mock
    private StudentResultRepository repository;

    @InjectMocks
    private StudentResultService service;

    private StudentResult sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new StudentResult(
                "STU-101",
                "Aarav Sharma",
                "Class 10th - Section A",
                "aarav@example.com",
                95, 90, 85, 95, 85
        );
    }

    @Test
    @DisplayName("Should successfully calculate total, percentage, grade, and status for Distinction")
    void testCalculateResults_Distinction() {
        StudentResult student = new StudentResult(
                "TEST-01", "Test Student", "Class 10", "test@test.com",
                95, 90, 92, 94, 96
        );

        assertEquals(467, student.getTotalMarks());
        assertEquals(93.4, student.getPercentage());
        assertEquals("A+", student.getGrade());
        assertEquals("PASS", student.getStatus());
    }

    @Test
    @DisplayName("Should mark student as FAIL if any subject is below passing threshold")
    void testCalculateResults_SubjectFailure() {
        StudentResult student = new StudentResult(
                "FAIL-01", "Failing Student", "Class 10", "fail@test.com",
                25, 90, 92, 94, 96
        );

        assertEquals("FAIL", student.getStatus());
        assertEquals("F", student.getGrade());
    }

    @Test
    @DisplayName("Should retrieve student result by roll number")
    void testGetResultByRollNumber() {
        when(repository.findByRollNumberIgnoreCase("STU-101")).thenReturn(Optional.of(sampleStudent));

        Optional<StudentResult> result = service.getResultByRollNumber("STU-101");

        assertTrue(result.isPresent());
        assertEquals("Aarav Sharma", result.get().getStudentName());
        verify(repository, times(1)).findByRollNumberIgnoreCase("STU-101");
    }

    @Test
    @DisplayName("Should throw exception when creating student with duplicate roll number")
    void testCreateResult_DuplicateRollNumber() {
        when(repository.existsByRollNumberIgnoreCase("STU-101")).thenReturn(true);

        StudentResultRequest req = new StudentResultRequest(
                "STU-101", "Another Student", "Class 10", "another@example.com",
                80, 80, 80, 80, 80
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createResult(req));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should create student successfully when roll number is unique")
    void testCreateResult_Success() {
        when(repository.existsByRollNumberIgnoreCase("STU-999")).thenReturn(false);
        when(repository.save(any(StudentResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentResultRequest req = new StudentResultRequest(
                "STU-999", "New Student", "Class 10", "new@example.com",
                75, 80, 85, 90, 70
        );

        StudentResult created = service.createResult(req);

        assertNotNull(created);
        assertEquals("STU-999", created.getRollNumber());
        assertEquals(400, created.getTotalMarks());
        assertEquals(80.0, created.getPercentage());
        assertEquals("A", created.getGrade());
        assertEquals("PASS", created.getStatus());
    }

    @Test
    @DisplayName("Should return accurate aggregate statistics")
    void testGetStatistics() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByStatus("PASS")).thenReturn(8L);
        when(repository.countByStatus("FAIL")).thenReturn(2L);
        when(repository.findAveragePercentage()).thenReturn(78.5);
        when(repository.findHighestMarks()).thenReturn(480);

        StudentStatsResponse stats = service.getStatistics();

        assertEquals(10, stats.getTotalStudents());
        assertEquals(8, stats.getPassedStudents());
        assertEquals(2, stats.getFailedStudents());
        assertEquals(80.0, stats.getPassPercentage());
        assertEquals(78.5, stats.getAveragePercentage());
        assertEquals(480, stats.getHighestMarks());
    }
}
