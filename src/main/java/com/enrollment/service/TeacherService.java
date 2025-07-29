package com.enrollment.service;
import com.enrollment.dto.TeacherDTO;
import com.enrollment.exception.CourseNotFoundException;
import com.enrollment.exception.TeacherNotFoundException;
import com.enrollment.exception.ValidationException;
import com.enrollment.model.Course;
import com.enrollment.model.Teacher;
import com.enrollment.repository.CourseRepository;
import com.enrollment.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * Creates a new teacher.
     * @param teacherDTO The DTO containing teacher details.
     * @return The created Teacher entity.
     */
    @Transactional
    public Teacher createTeacher(TeacherDTO teacherDTO) {
        // Admin only operation: Admin can add teachers
        Teacher teacher = new Teacher();
        teacher.setTeacherName(teacherDTO.getTeacherName());
        return teacherRepository.save(teacher);
    }

    /**
     * Updates an existing teacher.
     * @param teacherId The ID of the teacher to update.
     * @param teacherDTO The DTO containing updated teacher details.
     * @return The updated Teacher entity.
     */
    @Transactional
    public Teacher updateTeacher(Long teacherId, TeacherDTO teacherDTO) {
        // Admin only operation: Admin can update teachers
        Teacher existingTeacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + teacherId));

        existingTeacher.setTeacherName(teacherDTO.getTeacherName());
        return teacherRepository.save(existingTeacher);
    }

    /**
     * Deletes a teacher.
     * @param teacherId The ID of the teacher to delete.
     */
    @Transactional
    public void deleteTeacher(Long teacherId) {
        // Admin only operation: Admin can remove teachers
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + teacherId));

        // Disassociate courses from this teacher
        for (Course course : teacher.getCoursesTeaching()) {
            course.setTeacher(null); // Set teacher to null for courses they were teaching
            courseRepository.save(course); // Save the updated course
        }
        teacher.getCoursesTeaching().clear(); // Clear the set

        teacherRepository.delete(teacher);
    }

    /**
     * Retrieves a teacher by their ID.
     * @param teacherId The ID of the teacher.
     * @return The Teacher entity.
     */
    public Teacher getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + teacherId));
    }

    /**
     * Retrieves all teachers.
     * @return A list of all Teacher entities.
     */
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * Assigns a teacher to a course.
     * @param teacherId The ID of the teacher.
     * @param courseId The ID of the course.
     * @return The updated Teacher entity.
     */
    @Transactional
    public Teacher assignTeacherToCourse(Long teacherId, Long courseId) {
        // Admin only operation: Admin can assign teachers to courses
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + teacherId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        // Constraint: A teacher can be assigned to a maximum of 5 courses.
        if (teacher.getCoursesTeaching().size() >= 5 && !teacher.getCoursesTeaching().contains(course)) {
            throw new ValidationException("Teacher " + teacher.getTeacherName() + " is already assigned to maximum courses (5).");
        }

        // If the course already has a teacher, remove the association from the old teacher
        if (course.getTeacher() != null && !course.getTeacher().equals(teacher)) {
            course.getTeacher().getCoursesTeaching().remove(course);
        }

        teacher.addCourse(course); // Uses helper method in Teacher entity
        courseRepository.save(course); // Save course to update its teacher reference
        return teacherRepository.save(teacher);
    }

    /**
     * Unassigns a teacher from a course.
     * @param teacherId The ID of the teacher.
     * @param courseId The ID of the course.
     * @return The updated Teacher entity.
     */
    @Transactional
    public Teacher unassignTeacherFromCourse(Long teacherId, Long courseId) {
        // Admin only operation: Admin can unassign teachers from courses
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + teacherId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        if (!teacher.getCoursesTeaching().contains(course)) {
            throw new ValidationException("Teacher " + teacher.getTeacherName() + " is not assigned to course " + course.getCourseName() + ".");
        }

        teacher.removeCourse(course); // Uses helper method in Teacher entity
        courseRepository.save(course); // Save course to update its teacher reference
        return teacherRepository.save(teacher);
    }
}

