
package com.clubmanagement.backend.student;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    private final StudentRepository repository;

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/register")
    public Map<String, String> register(
            @RequestBody Student student) {

        if (student.getName() == null ||
            student.getRollNumber() == null ||
            student.getPassword() == null ||
            student.getName().isBlank() ||
            student.getRollNumber().isBlank() ||
            student.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Name, roll number and password are required");
        }

        boolean rollNumberExists = repository.findAll()
                .stream()
                .anyMatch(s -> s.getRollNumber()
                        .equalsIgnoreCase(student.getRollNumber()));

        if (rollNumberExists) {
            throw new IllegalArgumentException(
                    "This roll number is already registered");
        }

        student.setPassword(
                encoder.encode(student.getPassword()));

        repository.save(student);

        return Map.of(
                "message", "Registration successful");
    }

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody Map<String, String> request) {

        String name = request.get("name");
        String rollNumber = request.get("rollNumber");
        String password = request.get("password");

        if (name == null || rollNumber == null ||
            password == null) {

            throw new IllegalArgumentException(
                    "Name, roll number and password are required");
        }

        Student student = repository
                .findByNameIgnoreCaseAndRollNumber(
                        name, rollNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid student details"));

        if (!encoder.matches(
                password, student.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid student details");
        }

        return Map.of(
                "message", "Login successful",
                "name", student.getName(),
                "rollNumber", student.getRollNumber(),
                "studentId",
                String.valueOf(student.getId())
        );
    }
}
