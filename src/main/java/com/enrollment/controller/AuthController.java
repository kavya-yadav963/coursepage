package com.enrollment.controller;
import com.enrollment.config.JwtUtil;
import com.enrollment.dto.AuthRequest;
import com.enrollment.dto.AuthResponse;
import com.enrollment.dto.RegisterRequest;
import com.enrollment.model.User;
import com.enrollment.repository.UserRepository;
import com.enrollment.service.AuthService;
import com.enrollment.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthRequest authRequest) throws Exception {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final User user = userRepository.findByEmail(authRequest.getEmail()).get();
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt, user.getProfileId() != null ? user.getProfileId() : user.getId(), user.getName(), user.getRole()));
    }

    @PostMapping("/register/{role}")
    public ResponseEntity<?> registerUser(@PathVariable String role, @Valid @RequestBody RegisterRequest registerRequest) {
        authService.registerUser(role, registerRequest);
        return ResponseEntity.ok("User registered successfully!");
    }
}
