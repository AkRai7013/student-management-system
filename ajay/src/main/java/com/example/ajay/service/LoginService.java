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

    public ResponseGlobal<?> login(String email, String password) {

        if (email == null || password == null) {
            return ResponseGlobal.onFailure(
                    "Email and password must not be null"
            );
        }

        // Find user by email
        Login user = loginRepository.findByEmail(email);

        if (user == null) {
            return ResponseGlobal.onFailure(
                    "User not found with email: " + email
            );
        }

        // Check encrypted password
        boolean passwordMatches =
                passwordEncoder.matches(
                        password,
                        user.getPassword()
                );

        if (!passwordMatches) {
            return ResponseGlobal.onFailure(
                    "Invalid password for email: " + email
            );
        }

        // Generate JWT
        String jwt = jwtService.generateToken(
                user.getEmail(),
                user.getName()
        );

        return ResponseGlobal.onSuccess(
                "Login successful",
                jwt
        );
    }
}