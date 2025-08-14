package com.enrollment.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseCreateRequest {
    @NotBlank(message = "Course name is required")
    private String courseName;

    @Size(max = 30, message = "Max students per course is 30")
    private int maxStudents = 30;
}
