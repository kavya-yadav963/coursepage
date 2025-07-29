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
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teacherId;
    private String teacherName;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Course> coursesTeaching = new HashSet<>();

    // Helper method to add a course
    public void addCourse(Course course) {
        this.coursesTeaching.add(course);
        course.setTeacher(this);
    }

    // Helper method to remove a course
    public void removeCourse(Course course) {
        this.coursesTeaching.remove(course);
        course.setTeacher(null);
    }
}