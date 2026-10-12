package com.studentportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_results")
public class StudentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", unique = true, nullable = false)
    private String rollNumber;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "grade_class", nullable = false)
    private String gradeClass;

    @Column(name = "email")
    private String email;

    @Column(nullable = false)
    private Integer mathematics;

    @Column(nullable = false)
    private Integer science;

    @Column(nullable = false)
    private Integer english;

    @Column(name = "computer_science", nullable = false)
    private Integer computerScience;

    @Column(name = "social_studies", nullable = false)
    private Integer socialStudies;

    @Column(name = "total_marks")
    private Integer totalMarks;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "grade")
    private String grade;

    @Column(name = "status")
    private String status;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public StudentResult() {
        this.createdAt = LocalDateTime.now();
    }

    public StudentResult(String rollNumber, String studentName, String gradeClass, String email,
                         Integer mathematics, Integer science, Integer english,
                         Integer computerScience, Integer socialStudies) {
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.gradeClass = gradeClass;
        this.email = email;
        this.mathematics = mathematics;
        this.science = science;
        this.english = english;
        this.computerScience = computerScience;
        this.socialStudies = socialStudies;
        this.createdAt = LocalDateTime.now();
        calculateResults();
    }

    @PrePersist
    @PreUpdate
    public void calculateResults() {
        int m = mathematics != null ? mathematics : 0;
        int s = science != null ? science : 0;
        int e = english != null ? english : 0;
        int cs = computerScience != null ? computerScience : 0;
        int ss = socialStudies != null ? socialStudies : 0;

        this.totalMarks = m + s + e + cs + ss;
        this.percentage = Math.round((this.totalMarks / 5.0) * 100.0) / 100.0;

        // Passing condition: minimum 35 marks in each subject and percentage >= 40
        boolean passed = m >= 35 && s >= 35 && e >= 35 && cs >= 35 && ss >= 35 && this.percentage >= 40.0;
        this.status = passed ? "PASS" : "FAIL";

        if (!passed) {
            this.grade = "F";
            this.remarks = "Needs Improvement / Supplementary Required";
        } else if (this.percentage >= 90.0) {
            this.grade = "A+";
            this.remarks = "Outstanding Performance (Distinction)";
        } else if (this.percentage >= 80.0) {
            this.grade = "A";
            this.remarks = "Excellent Performance (First Division)";
        } else if (this.percentage >= 70.0) {
            this.grade = "B+";
            this.remarks = "Very Good Performance";
        } else if (this.percentage >= 60.0) {
            this.grade = "B";
            this.remarks = "Good Performance (Second Division)";
        } else if (this.percentage >= 50.0) {
            this.grade = "C";
            this.remarks = "Average Performance";
        } else {
            this.grade = "D";
            this.remarks = "Pass (Third Division)";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGradeClass() {
        return gradeClass;
    }

    public void setGradeClass(String gradeClass) {
        this.gradeClass = gradeClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getMathematics() {
        return mathematics;
    }

    public void setMathematics(Integer mathematics) {
        this.mathematics = mathematics;
    }

    public Integer getScience() {
        return science;
    }

    public void setScience(Integer science) {
        this.science = science;
    }

    public Integer getEnglish() {
        return english;
    }

    public void setEnglish(Integer english) {
        this.english = english;
    }

    public Integer getComputerScience() {
        return computerScience;
    }

    public void setComputerScience(Integer computerScience) {
        this.computerScience = computerScience;
    }

    public Integer getSocialStudies() {
        return socialStudies;
    }

    public void setSocialStudies(Integer socialStudies) {
        this.socialStudies = socialStudies;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
