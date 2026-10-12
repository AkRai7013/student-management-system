package com.example.ajay.service;

import com.example.ajay.Configuration.JWTService;
import com.example.ajay.entity.Login;
import com.example.ajay.repo.LoginRepository;
import com.example.ajay.response.ResponseGlobal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final LoginRepository loginRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginService(
            LoginRepository loginRepository,
            JWTService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.loginRepository = loginRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // Student Login
    public ResponseGlobal<?> login(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            return ResponseGlobal.onFailure(
                    "Email and password are required"
            );
        }

        // Find user by email
        Login user = loginRepository.findByEmail(email.trim());

        if (user == null) {
            return ResponseGlobal.onFailure(
                    "Invalid email or password"
            );
        }

        // Validate encrypted password
        boolean passwordMatches = passwordEncoder.matches(
                password,
                user.getPassword()
        );

        if (!passwordMatches) {
            return ResponseGlobal.onFailure(
                    "Invalid email or password"
            );
        }

        // Generate JWT token
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getName()
        );

        return ResponseGlobal.onSuccess(
                "Login successful",
                token
        );
    }
}