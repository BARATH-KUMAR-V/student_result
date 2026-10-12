package com.studentportal.dto;

public class StudentStatsResponse {
    private long totalStudents;
    private long passedStudents;
    private long failedStudents;
    private double passPercentage;
    private double averagePercentage;
    private int highestMarks;

    public StudentStatsResponse() {}

    public StudentStatsResponse(long totalStudents, long passedStudents, long failedStudents,
                                double passPercentage, double averagePercentage, int highestMarks) {
        this.totalStudents = totalStudents;
        this.passedStudents = passedStudents;
        this.failedStudents = failedStudents;
        this.passPercentage = passPercentage;
        this.averagePercentage = averagePercentage;
        this.highestMarks = highestMarks;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getPassedStudents() {
        return passedStudents;
    }

    public void setPassedStudents(long passedStudents) {
        this.passedStudents = passedStudents;
    }

    public long getFailedStudents() {
        return failedStudents;
    }

    public void setFailedStudents(long failedStudents) {
        this.failedStudents = failedStudents;
    }

    public double getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(double passPercentage) {
        this.passPercentage = passPercentage;
    }

    public double getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(double averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public int getHighestMarks() {
        return highestMarks;
    }

    public void setHighestMarks(int highestMarks) {
        this.highestMarks = highestMarks;
    }
}
