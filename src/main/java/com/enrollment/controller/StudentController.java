package com.enrollment.controller;

import com.enrollment.model.Student;
import com.enrollment.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Test endpoint to verify controller is working
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Student Controller is working!");
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        try {
            System.out.println("Getting all students...");
            List<Student> students = studentService.getAllStudents();
            System.out.println("Found " + students.size() + " students");
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            System.err.println("Error getting all students: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        System.out.println("Getting student by ID: " + id);
        return studentService.getStudentById(id)
                .map(student -> {
                    System.out.println("Found student: " + student);
                    return ResponseEntity.ok(student);
                })
                .orElseGet(() -> {
                    System.out.println("Student not found with ID: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        System.out.println("=== CREATE STUDENT ENDPOINT CALLED ===");
        System.out.println("Received student data: " + student);

        try {
            // Validate input
            if (student == null) {
                System.err.println("Student object is null");
                return ResponseEntity.badRequest().build();
            }

            if (student.getStudentName() == null || student.getStudentName().trim().isEmpty()) {
                System.err.println("Student name is null or empty");
                return ResponseEntity.badRequest().build();
            }

            System.out.println("Creating student with name: " + student.getStudentName());
            Student createdStudent = studentService.createStudent(student);
            System.out.println("Student created successfully: " + createdStudent);

            return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating student: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        System.out.println("Updating student with ID: " + id);
        try {
            Student updatedStudent = studentService.updateStudent(id, student);
            System.out.println("Student updated successfully: " + updatedStudent);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            System.err.println("Error updating student: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        System.out.println("Deleting student with ID: " + id);
        try {
            studentService.deleteStudent(id);
            System.out.println("Student deleted successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error deleting student: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}