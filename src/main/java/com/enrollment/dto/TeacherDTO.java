package com.enrollment.dto;
import lombok.Data;
import java.util.Set;

@Data
public class TeacherDTO {
    private Long teacherId;
    private String teacherName;
    private Set<Long> courseIds;
}
