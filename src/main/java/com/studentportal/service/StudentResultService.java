package com.studentportal.service;

import com.studentportal.dto.StudentResultRequest;
import com.studentportal.dto.StudentStatsResponse;
import com.studentportal.model.StudentResult;
import com.studentportal.repository.StudentResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentResultService {

    private final StudentResultRepository repository;

    @Autowired
    public StudentResultService(StudentResultRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<StudentResult> getAllResults() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<StudentResult> getResultById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<StudentResult> getResultByRollNumber(String rollNumber) {
        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            return Optional.empty();
        }
        return repository.findByRollNumberIgnoreCase(rollNumber.trim());
    }

    @Transactional(readOnly = true)
    public List<StudentResult> searchResults(String query) {
        if (query == null || query.trim().isEmpty()) {
            return repository.findAll();
        }
        String q = query.trim();
        return repository.findByStudentNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(q, q);
    }

    public StudentResult createResult(StudentResultRequest req) {
        String rollNumber = req.getRollNumber().trim().toUpperCase();
        if (repository.existsByRollNumberIgnoreCase(rollNumber)) {
            throw new IllegalArgumentException("Student with Roll Number '" + rollNumber + "' already exists.");
        }

        StudentResult student = new StudentResult(
                rollNumber,
                req.getStudentName().trim(),
                req.getGradeClass().trim(),
                req.getEmail() != null ? req.getEmail().trim() : null,
                req.getMathematics(),
                req.getScience(),
                req.getEnglish(),
                req.getComputerScience(),
                req.getSocialStudies()
        );

        return repository.save(student);
    }

    public StudentResult updateResult(Long id, StudentResultRequest req) {
        StudentResult student = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student record not found with ID: " + id));

        String newRoll = req.getRollNumber().trim().toUpperCase();
        if (!student.getRollNumber().equalsIgnoreCase(newRoll) && repository.existsByRollNumberIgnoreCase(newRoll)) {
            throw new IllegalArgumentException("Roll Number '" + newRoll + "' is already assigned to another student.");
        }

        student.setRollNumber(newRoll);
        student.setStudentName(req.getStudentName().trim());
        student.setGradeClass(req.getGradeClass().trim());
        student.setEmail(req.getEmail() != null ? req.getEmail().trim() : null);
        student.setMathematics(req.getMathematics());
        student.setScience(req.getScience());
        student.setEnglish(req.getEnglish());
        student.setComputerScience(req.getComputerScience());
        student.setSocialStudies(req.getSocialStudies());

        student.calculateResults();
        return repository.save(student);
    }

    public void deleteResult(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Student record not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public StudentStatsResponse getStatistics() {
        long total = repository.count();
        if (total == 0) {
            return new StudentStatsResponse(0, 0, 0, 0.0, 0.0, 0);
        }

        long passed = repository.countByStatus("PASS");
        long failed = repository.countByStatus("FAIL");
        double passPercentage = Math.round(((double) passed / total) * 10000.0) / 100.0;

        Double avg = repository.findAveragePercentage();
        double avgPercentage = avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;

        Integer maxMarks = repository.findHighestMarks();
        int highestMarks = maxMarks != null ? maxMarks : 0;

        return new StudentStatsResponse(total, passed, failed, passPercentage, avgPercentage, highestMarks);
    }
}
