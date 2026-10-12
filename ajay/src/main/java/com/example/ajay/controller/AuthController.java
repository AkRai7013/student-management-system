package com.example.ajay.controller;

import com.example.ajay.Configuration.JWTService;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.LoginService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final LoginService loginService;
    private final JWTService jwtService;

    public AuthController(
            LoginService loginService,
            JWTService jwtService
    ) {
        this.loginService = loginService;
        this.jwtService = jwtService;
    }

    // Student Login
    @PostMapping("/StudentLogin")
    public ResponseGlobal<?> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        return loginService.login(email, password);
    }

    // Get name from JWT token
    @GetMapping("/name")
    public ResponseGlobal<String> getNameByToken(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = extractToken(authorization);

        String name = jwtService.getNameByToken(token);

        return ResponseGlobal.onSuccess(
                "Name fetched from token",
                name
        );
    }

    // Get email from JWT token
    @GetMapping("/email")
    public ResponseGlobal<String> getEmailByToken(
            @RequestHeader("Authorization") String authorization
    ) {
        String token = extractToken(authorization);

        String email = jwtService.getEmailByToken(token);

        return ResponseGlobal.onSuccess(
                "Email fetched from token",
                email
        );
    }

    // Extract token from Authorization header
    private String extractToken(String authorization) {

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException(
                    "Authorization header must contain a Bearer token"
            );
        }

        return authorization.substring(7);
    }
}