package com.enrollment.service;

import com.enrollment.dto.StudentDTO;
import com.enrollment.exception.StudentNotFoundException;
import com.enrollment.exception.ValidationException;
import com.enrollment.model.Course;
import com.enrollment.model.Student;
import com.enrollment.repository.CourseRepository;
import com.enrollment.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository; // Needed for display information

    @Autowired
    public StudentService(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Creates a new student.
     * @param studentDTO The DTO containing student details.
     * @return The created Student entity.
     */
    @Transactional
    public Student createStudent(StudentDTO studentDTO) {
        // Admin only operation: Admin can add students
        Student student = new Student();
        student.setStudentName(studentDTO.getStudentName());
        return studentRepository.save(student);
    }

    /**
     * Updates an existing student.
     * @param studentId The ID of the student to update.
     * @param studentDTO The DTO containing updated student details.
     * @return The updated Student entity.
     */
    @Transactional
    public Student updateStudent(Long studentId, StudentDTO studentDTO) {
        // Admin only operation: Admin can update students
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        existingStudent.setStudentName(studentDTO.getStudentName());
        return studentRepository.save(existingStudent);
    }

    /**
     * Deletes a student.
     * @param studentId The ID of the student to delete.
     */
    @Transactional
    public void deleteStudent(Long studentId) {
        // Admin only operation: Admin can remove students
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        // Remove associations from courses
        for (Course course : student.getEnrolledCourses()) {
            course.getEnrolledStudents().remove(student);
        }
        student.getEnrolledCourses().clear(); // Clear the set to remove join table entries

        studentRepository.delete(student);
    }

    /**
     * Retrieves a student by their ID.
     * @param studentId The ID of the student.
     * @return The Student entity.
     */
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));
    }

    /**
     * Retrieves all students.
     * @return A list of all Student entities.
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
}