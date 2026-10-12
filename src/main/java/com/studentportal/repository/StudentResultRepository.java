package com.studentportal.repository;

import com.studentportal.model.StudentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {

    Optional<StudentResult> findByRollNumberIgnoreCase(String rollNumber);

    boolean existsByRollNumberIgnoreCase(String rollNumber);

    List<StudentResult> findByStudentNameContainingIgnoreCaseOrRollNumberContainingIgnoreCase(String name, String rollNumber);

    long countByStatus(String status);

    @Query("SELECT AVG(s.percentage) FROM StudentResult s")
    Double findAveragePercentage();

    @Query("SELECT MAX(s.totalMarks) FROM StudentResult s")
    Integer findHighestMarks();
}
