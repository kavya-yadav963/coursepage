package com.enrollment.service;

import com.enrollment.dto.RegisterRequest;
import com.enrollment.exception.ValidationException;
import com.enrollment.model.Student;
import com.enrollment.model.Teacher;
import com.enrollment.model.User;
import com.enrollment.repository.StudentRepository;
import com.enrollment.repository.TeacherRepository;
import com.enrollment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String role, RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ValidationException("Email is already in use!");
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        String userRole = "ROLE_" + role.toUpperCase();

        User newUser = new User(registerRequest.getName(), registerRequest.getEmail(), encodedPassword, userRole);

        // Create a corresponding Student or Teacher profile
        if ("ROLE_STUDENT".equals(userRole)) {
            Student student = new Student();
            student.setStudentName(registerRequest.getName());
            student.setEmail(registerRequest.getEmail());
            Student savedStudent = studentRepository.save(student);
            newUser.setProfileId(savedStudent.getStudentId());
        } else if ("ROLE_TEACHER".equals(userRole)) {
            Teacher teacher = new Teacher();
            teacher.setTeacherName(registerRequest.getName());
            teacher.setEmail(registerRequest.getEmail());
            Teacher savedTeacher = teacherRepository.save(teacher);
            newUser.setProfileId(savedTeacher.getTeacherId());
        }

        return userRepository.save(newUser);
    }
}