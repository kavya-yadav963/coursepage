package com.enrollment.service;
import com.enrollment.dto.CourseDTO;
import com.enrollment.exception.CourseNotFoundException;
import com.enrollment.exception.StudentNotFoundException;
import com.enrollment.exception.TeacherNotFoundException;
import com.enrollment.exception.ValidationException;
import com.enrollment.model.Course;
import com.enrollment.model.Student;
import com.enrollment.model.Teacher;
import com.enrollment.repository.CourseRepository;
import com.enrollment.repository.StudentRepository;
import com.enrollment.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, TeacherRepository teacherRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Creates a new course.
     * @param courseDTO The DTO containing course details.
     * @return The created Course entity.
     */
    @Transactional
    public Course createCourse(CourseDTO courseDTO) {
        // Admin only operation: Admin can add courses
        // In a real application, you'd check user roles here (e.g., SecurityContextHolder.getContext().getAuthentication().getAuthorities())

        Course course = new Course();
        course.setCourseName(courseDTO.getCourseName());
        course.setMaxStudents(courseDTO.getMaxStudents());

        if (courseDTO.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(courseDTO.getTeacherId())
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + courseDTO.getTeacherId()));

            // Constraint: A teacher can be assigned to a maximum of 5 courses.
            if (teacher.getCoursesTeaching().size() >= 5) {
                throw new ValidationException("Teacher " + teacher.getTeacherName() + " is already assigned to maximum courses (5).");
            }
            course.setTeacher(teacher);
            teacher.getCoursesTeaching().add(course); // Add course to teacher's list
        }

        return courseRepository.save(course);
    }

    /**
     * Updates an existing course.
     * @param courseId The ID of the course to update.
     * @param courseDTO The DTO containing updated course details.
     * @return The updated Course entity.
     */
    @Transactional
    public Course updateCourse(Long courseId, CourseDTO courseDTO) {
        // Admin only operation: Admin can update courses
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        existingCourse.setCourseName(courseDTO.getCourseName());
        existingCourse.setMaxStudents(courseDTO.getMaxStudents());

        if (courseDTO.getTeacherId() != null) {
            Teacher newTeacher = teacherRepository.findById(courseDTO.getTeacherId())
                    .orElseThrow(() -> new TeacherNotFoundException("Teacher not found with ID: " + courseDTO.getTeacherId()));

            // Remove course from old teacher if exists
            if (existingCourse.getTeacher() != null && !existingCourse.getTeacher().equals(newTeacher)) {
                existingCourse.getTeacher().getCoursesTeaching().remove(existingCourse);
            }

            // Constraint: A teacher can be assigned to a maximum of 5 courses.
            if (newTeacher.getCoursesTeaching().size() >= 5 && !newTeacher.getCoursesTeaching().contains(existingCourse)) {
                throw new ValidationException("Teacher " + newTeacher.getTeacherName() + " is already assigned to maximum courses (5).");
            }
            existingCourse.setTeacher(newTeacher);
            newTeacher.getCoursesTeaching().add(existingCourse); // Add course to new teacher's list
        } else {
            // If teacherId is null, disassociate from existing teacher
            if (existingCourse.getTeacher() != null) {
                existingCourse.getTeacher().getCoursesTeaching().remove(existingCourse);
                existingCourse.setTeacher(null);
            }
        }

        return courseRepository.save(existingCourse);
    }

    /**
     * Deletes a course.
     * @param courseId The ID of the course to delete.
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        // Admin only operation: Admin can remove courses
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));

        // Remove associations from students
        for (Student student : course.getEnrolledStudents()) {
            student.getEnrolledCourses().remove(course);
        }
        course.getEnrolledStudents().clear(); // Clear the set to remove join table entries

        // Remove association from teacher
        if (course.getTeacher() != null) {
            course.getTeacher().getCoursesTeaching().remove(course);
            course.setTeacher(null);
        }

        courseRepository.delete(course);
    }

    /**
     * Retrieves a course by its ID.
     * @param courseId The ID of the course.
     * @return The Course entity.
     */
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));
    }

    /**
     * Retrieves all courses.
     * @return A list of all Course entities.
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Adds a student to a course.
     * @param courseId The ID of the course.
     * @param studentId The ID of the student.
     * @param requestingUserId The ID of the user making the request (for authorization).
     * @param requestingUserRole The role of the user making the request (for authorization).
     * @return The updated Course entity.
     */
    @Transactional
    public Course addStudentToCourse(Long courseId, Long studentId, Long requestingUserId, String requestingUserRole) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        // Constraint: Student will have only view rights, they cannot add themselves.
        // This method is called by Admin or Teacher.

        // Admin can add any student to any course
        if ("ADMIN".equalsIgnoreCase(requestingUserRole)) {
            // No additional checks needed for Admin
        }
        // Teacher can add students only to courses they teach
        else if ("TEACHER".equalsIgnoreCase(requestingUserRole)) {
            if (course.getTeacher() == null || !course.getTeacher().getTeacherId().equals(requestingUserId)) {
                throw new ValidationException("Teacher can only add students to courses they are teaching.");
            }
        } else {
            throw new ValidationException("Unauthorized: Only Admin or the assigned Teacher can add students to a course.");
        }


        // Constraint: A course can have a maximum of 30 students.
        if (course.getEnrolledStudents().size() >= course.getMaxStudents()) {
            throw new ValidationException("Course " + course.getCourseName() + " has reached its maximum student limit (" + course.getMaxStudents() + ").");
        }

        if (course.getEnrolledStudents().contains(student)) {
            throw new ValidationException("Student " + student.getStudentName() + " is already enrolled in course " + course.getCourseName() + ".");
        }

        course.addStudent(student); // Uses helper method in Course entity
        return courseRepository.save(course);
    }

    /**
     * Removes a student from a course.
     * @param courseId The ID of the course.
     * @param studentId The ID of the student.
     * @param requestingUserId The ID of the user making the request (for authorization).
     * @param requestingUserRole The role of the user making the request (for authorization).
     * @return The updated Course entity.
     */
    @Transactional
    public Course removeStudentFromCourse(Long courseId, Long studentId, Long requestingUserId, String requestingUserRole) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with ID: " + courseId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with ID: " + studentId));

        // Constraint: Student will have only view rights, they cannot remove themselves.
        // This method is called by Admin or Teacher.

        // Admin can remove any student from any course
        if ("ADMIN".equalsIgnoreCase(requestingUserRole)) {
            // No additional checks needed for Admin
        }
        // Only Teacher of corresponding course can unroll a student.
        else if ("TEACHER".equalsIgnoreCase(requestingUserRole)) {
            if (course.getTeacher() == null || !course.getTeacher().getTeacherId().equals(requestingUserId)) {
                throw new ValidationException("Teacher can only remove students from courses they are teaching.");
            }
        } else {
            throw new ValidationException("Unauthorized: Only Admin or the assigned Teacher can remove students from a course.");
        }

        if (!course.getEnrolledStudents().contains(student)) {
            throw new ValidationException("Student " + student.getStudentName() + " is not enrolled in course " + course.getCourseName() + ".");
        }

        course.removeStudent(student); // Uses helper method in Course entity
        return courseRepository.save(course);
    }
}

