package com.enrollment.service;

import com.enrollment.model.*;
import com.enrollment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private AdminRepository adminRepository;

    public User registerUser(User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Save user first
        User savedUser = userRepository.save(user);

        // Create corresponding role-specific entity
        Long roleSpecificId = createRoleSpecificEntity(savedUser);
        savedUser.setRoleSpecificId(roleSpecificId);

        return userRepository.save(savedUser);
    }

    private Long createRoleSpecificEntity(User user) {
        switch (user.getRole()) {
            case STUDENT:
                Student student = new Student(user.getName());
                student = studentRepository.save(student);
                return student.getStudentId();

            case TEACHER:
                Teacher teacher = new Teacher(user.getName());
                teacher = teacherRepository.save(teacher);
                return teacher.getTeacherId();

            case ADMIN:
                Admin admin = new Admin(user.getName());
                admin = adminRepository.save(admin);
                return admin.getAdminId();

            default:
                throw new RuntimeException("Invalid role");
        }
    }

    public Optional<User> authenticateUser(String username, String password, UserRole role) {
        Optional<User> userOpt = userRepository.findByUsernameAndRole(username, role);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Simple password check (in production, use proper password hashing)
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}