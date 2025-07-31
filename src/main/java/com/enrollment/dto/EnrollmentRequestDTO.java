package com.enrollment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDTO {
    @NotNull(message = "Student ID is mandatory")
    private Long studentId;
    @NotNull(message = "Course ID is mandatory")
    private Long courseId;
}