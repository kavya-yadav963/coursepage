package com.enrollment.service;

import com.enrollment.entity.Course;
import com.enrollment.entity.Student;
import com.enrollment.entity.Teacher;
import com.enrollment.repository.CourseRepository;
import com.enrollment.repository.StudentRepository;
import com.enrollment.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void removeCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public boolean addStudentToCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        Student student = studentRepository.findById(studentId).orElse(null);
        if (course == null || student == null) return false;
        if (course.getStudents().size() >= course.getMaxStudents()) return false;
        course.getStudents().add(student);
        student.getCourses().add(course);
        courseRepository.save(course);
        studentRepository.save(student);
        return true;
    }

    @Transactional
    public boolean removeStudentFromCourse(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        Student student = studentRepository.findById(studentId).orElse(null);
        if (course == null || student == null) return false;
        course.getStudents().remove(student);
        student.getCourses().remove(course);
        courseRepository.save(course);
        studentRepository.save(student);
        return true;
    }

    @Transactional
    public boolean assignTeacherToCourse(Long courseId, Long teacherId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (course == null || teacher == null) return false;
        if (teacher.getCourses().size() >= 5) return false;
        course.setTeacher(teacher);
        teacher.getCourses().add(course);
        courseRepository.save(course);
        teacherRepository.save(teacher);
        return true;
    }
}
