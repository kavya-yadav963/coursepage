package com.enrollment.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;
    private String studentName;

    @ManyToMany(mappedBy = "enrolledStudents", fetch = FetchType.LAZY)
    private Set<Course> enrolledCourses = new HashSet<>();

    // Helper method to enroll in a course
    public void enrollInCourse(Course course) {
        this.enrolledCourses.add(course);
        course.getEnrolledStudents().add(this);
    }

    // Helper method to withdraw from a course
    public void withdrawFromCourse(Course course) {
        this.enrolledCourses.remove(course);
        course.getEnrolledStudents().remove(this);
    }
}