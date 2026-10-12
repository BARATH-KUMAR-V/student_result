package com.studentportal.dto;

import jakarta.validation.constraints.*;

public class StudentResultRequest {

    @NotBlank(message = "Roll Number is required")
    @Size(min = 2, max = 30, message = "Roll Number must be between 2 and 30 characters")
    private String rollNumber;

    @NotBlank(message = "Student Name is required")
    @Size(min = 2, max = 100, message = "Student Name must be between 2 and 100 characters")
    private String studentName;

    @NotBlank(message = "Class/Semester is required")
    private String gradeClass;

    @Email(message = "Email must be valid if provided")
    private String email;

    @NotNull(message = "Mathematics marks is required")
    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Integer mathematics;

    @NotNull(message = "Science marks is required")
    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Integer science;

    @NotNull(message = "English marks is required")
    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Integer english;

    @NotNull(message = "Computer Science marks is required")
    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Integer computerScience;

    @NotNull(message = "Social Studies marks is required")
    @Min(value = 0, message = "Marks cannot be negative")
    @Max(value = 100, message = "Marks cannot exceed 100")
    private Integer socialStudies;

    public StudentResultRequest() {}

    public StudentResultRequest(String rollNumber, String studentName, String gradeClass, String email,
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
    }

    // Getters and Setters
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
}
