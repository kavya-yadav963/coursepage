package com.enrollment.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private Long teacherId; // For updates
    @NotBlank(message = "Teacher name is mandatory")
    private String teacherName;
}
