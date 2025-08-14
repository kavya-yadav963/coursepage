package com.enrollment.controller;
import com.enrollment.entity.Course;
import com.enrollment.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);
        return course.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Course addCourse(@RequestBody Course course) {
        return courseService.addCourse(course);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCourse(@PathVariable Long id) {
        courseService.removeCourse(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<String> addStudentToCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        boolean success = courseService.addStudentToCourse(courseId, studentId);
        return success ? ResponseEntity.ok("Student added to course.") : ResponseEntity.badRequest().body("Failed to add student.");
    }

    @DeleteMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<String> removeStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        boolean success = courseService.removeStudentFromCourse(courseId, studentId);
        return success ? ResponseEntity.ok("Student removed from course.") : ResponseEntity.badRequest().body("Failed to remove student.");
    }

    @PostMapping("/{courseId}/teacher/{teacherId}")
    public ResponseEntity<String> assignTeacherToCourse(@PathVariable Long courseId, @PathVariable Long teacherId) {
        boolean success = courseService.assignTeacherToCourse(courseId, teacherId);
        return success ? ResponseEntity.ok("Teacher assigned to course.") : ResponseEntity.badRequest().body("Failed to assign teacher.");
    }
}
