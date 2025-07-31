package com.enrollment.config;
import com.enrollment.model.User;
import com.enrollment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if an admin user already exists
        if (!userRepository.existsByEmail("admin@system.com")) {
            // Create the admin user if it doesn't exist
            String adminPassword = "admin-password"; // Use a strong password, maybe from env variables

            User adminUser = new User(
                    "Administrator",
                    "admin@system.com",
                    passwordEncoder.encode(adminPassword),
                    "ROLE_ADMIN"
            );

            // Admin user does not have a separate "profile", so profileId can be null
            userRepository.save(adminUser);

            System.out.println("==================================================================");
            System.out.println("Admin user created successfully!");
            System.out.println("Email: admin@system.com");
            System.out.println("Password: " + adminPassword + " (Please change this in a secure way)");
            System.out.println("==================================================================");
        }
    }
}
