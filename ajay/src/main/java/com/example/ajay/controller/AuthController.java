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

    @PostMapping("/StudentLogin")
    public ResponseGlobal<?> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        return loginService.login(email, password);
    }

    @GetMapping("/name")
    public ResponseGlobal<String> getNameByToken(
            @RequestHeader("Authorization") String authorization
    ) {

        String token = authorization.replace("Bearer ", "");

        String name = jwtService.getNameByToken(token);

        return ResponseGlobal.onSuccess(
                "Name fetched from token",
                name
        );
    }

    @GetMapping("/email")
    public ResponseGlobal<String> getEmailByToken(
            @RequestHeader("Authorization") String authorization
    ) {

        String token = authorization.replace("Bearer ", "");

        String email = jwtService.getEmailByToken(token);

        return ResponseGlobal.onSuccess(
                "Email fetched from token",
                email
        );
    }
}