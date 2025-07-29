package com.enrollment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long courseId; // For updates
    @NotBlank(message = "Course name is mandatory")
    private String courseName;
    @NotNull(message = "Maximum students is mandatory")
    @Min(value = 1, message = "Maximum students must be at least 1")
    private int maxStudents;
    private Long teacherId; // To assign a teacher when creating/updating a course
}
