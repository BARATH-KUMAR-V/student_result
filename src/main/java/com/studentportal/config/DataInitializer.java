package com.studentportal.config;

import com.studentportal.model.StudentResult;
import com.studentportal.repository.StudentResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(StudentResultRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<StudentResult> samples = List.of(
                        new StudentResult("STU-101", "Aarav Sharma",  "Class 10th - Section A", "aarav.sharma@example.com",  95, 92, 88, 98, 91),
                        new StudentResult("STU-102", "Priya Patel",   "Class 10th - Section A", "priya.patel@example.com",   85, 78, 82, 89, 76),
                        new StudentResult("STU-103", "Rahul Verma",   "Class 10th - Section B", "rahul.verma@example.com",   65, 70, 68, 72, 60),
                        new StudentResult("STU-104", "Sneha Reddy",   "Class 10th - Section B", "sneha.reddy@example.com",   74, 80, 75, 84, 72),
                        new StudentResult("STU-105", "Vikram Singh",  "Class 10th - Section C", "vikram.singh@example.com",  42, 38, 45, 50, 40),
                        new StudentResult("STU-106", "Rohan Gupta",   "Class 10th - Section C", "rohan.gupta@example.com",   28, 55, 60, 45, 52)
                );
                repository.saveAll(samples);
                log.info("Seeded {} student records.", samples.size());
            }
        };
    }
}
