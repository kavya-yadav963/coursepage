package com.enrollment.controller;
import com.enrollment.model.Course;
import com.enrollment.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Course-related operations (primarily view for non-admin/teacher).
 * Enrollment/withdrawal operations are handled by Admin/Teacher controllers.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    // Teacher can add/remove students from their courses
    // This endpoint would typically be protected for TEACHER role
    @PutMapping("/{courseId}/addStudent/{studentId}/byTeacher/{teacherId}")
    public ResponseEntity<Course> teacherAddStudentToCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @PathVariable Long teacherId // This would come from authenticated user context
    ) {
        // Dummy values for requesting user for demonstration. In a real app, get from SecurityContext.
        String teacherUserRole = "TEACHER";
        Course updatedCourse = courseService.addStudentToCourse(courseId, studentId, teacherId, teacherUserRole);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @PutMapping("/{courseId}/removeStudent/{studentId}/byTeacher/{teacherId}")
    public ResponseEntity<Course> teacherRemoveStudentFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @PathVariable Long teacherId // This would come from authenticated user context
    ) {
        // Dummy values for requesting user for demonstration. In a real app, get from SecurityContext.
        String teacherUserRole = "TEACHER";
        Course updatedCourse = courseService.removeStudentFromCourse(courseId, studentId, teacherId, teacherUserRole);
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }
}

