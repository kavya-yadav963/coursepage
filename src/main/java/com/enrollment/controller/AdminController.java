package com.enrollment.controller;
import com.enrollment.dto.CourseDTO;
import com.enrollment.dto.StudentDTO;
import com.enrollment.dto.TeacherDTO;
import com.enrollment.model.Course;
import com.enrollment.model.Student;
import com.enrollment.model.Teacher;
import com.enrollment.service.CourseService;
import com.enrollment.service.StudentService;
import com.enrollment.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Admin-specific operations.
 * Assumes an authenticated user with 'ADMIN' role.
 * In a real application, Spring Security would handle role-based access to these endpoints.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CourseService courseService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    @Autowired
    public AdminController(CourseService courseService, TeacherService teacherService, StudentService studentService) {
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    // --- Course Management (Admin Only) ---
    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        Course createdCourse = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseDTO courseDTO) {
        Course updatedCourse = courseService.updateCourse(id, courseDTO);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Teacher Management (Admin Only) ---
    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody TeacherDTO teacherDTO) {
        Teacher createdTeacher = teacherService.createTeacher(teacherDTO);
        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherDTO teacherDTO) {
        Teacher updatedTeacher = teacherService.updateTeacher(id, teacherDTO);
        return new ResponseEntity<>(updatedTeacher, HttpStatus.OK);
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/teachers/{teacherId}/assign/{courseId}")
    public ResponseEntity<Teacher> assignTeacherToCourse(@PathVariable Long teacherId, @PathVariable Long courseId) {
        Teacher updatedTeacher = teacherService.assignTeacherToCourse(teacherId, courseId);
        return new ResponseEntity<>(updatedTeacher, HttpStatus.OK);
    }

    @PutMapping("/teachers/{teacherId}/unassign/{courseId}")
    public ResponseEntity<Teacher> unassignTeacherFromCourse(@PathVariable Long teacherId, @PathVariable Long courseId) {
        Teacher updatedTeacher = teacherService.unassignTeacherFromCourse(teacherId, courseId);
        return new ResponseEntity<>(updatedTeacher, HttpStatus.OK);
    }

    // --- Student Management (Admin Only) ---
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        Student createdStudent = studentService.createStudent(studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDTO studentDTO) {
        Student updatedStudent = studentService.updateStudent(id, studentDTO);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Admin can add/remove students from any course
    @PutMapping("/courses/{courseId}/addStudent/{studentId}")
    public ResponseEntity<Course> adminAddStudentToCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        // Dummy values for requesting user for demonstration. In a real app, get from SecurityContext.
        Long adminUserId = 1L; // Example admin ID
        String adminUserRole = "ADMIN";
        Course updatedCourse = courseService.addStudentToCourse(courseId, studentId, adminUserId, adminUserRole);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @PutMapping("/courses/{courseId}/removeStudent/{studentId}")
    public ResponseEntity<Course> adminRemoveStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        // Dummy values for requesting user for demonstration. In a real app, get from SecurityContext.
        Long adminUserId = 1L; // Example admin ID
        String adminUserRole = "ADMIN";
        Course updatedCourse = courseService.removeStudentFromCourse(courseId, studentId, adminUserId, adminUserRole);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }
}
