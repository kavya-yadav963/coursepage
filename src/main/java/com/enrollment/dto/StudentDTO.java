package com.enrollment.dto;
import lombok.Data;
import java.util.Set;

@Data
public class StudentDTO {
    private Long studentId;
    private String studentName;
    private Set<Long> courseIds;
}
