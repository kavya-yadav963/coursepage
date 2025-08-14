package com.enrollment.dto;

import lombok.Data;
import java.util.Set;

@Data
public class CourseDTO {
    private Long courseId;
    private String courseName;
    private int maxStudents;
    private Set<Long> studentIds;
    private Long teacherId;
}
