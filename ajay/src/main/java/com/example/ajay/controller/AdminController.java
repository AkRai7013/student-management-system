package com.example.ajay.controller;

import com.example.ajay.DTO.AdminDTO;
import com.example.ajay.DTO.AdminLoginDTO;
import com.example.ajay.service.AdminService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Create Admin/Staff account
    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(
            @RequestBody AdminDTO adminDTO) {

        String response = adminService.createAdmin(adminDTO);

        if ("Admin account created successfully".equals(response)) {
            return ResponseEntity.ok(response);
        }

        if ("Admin email already exists".equals(response)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    // Admin/Staff login
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody AdminLoginDTO loginDTO) {

        try {
            Map<String, Object> response =
                    adminService.adminLogin(loginDTO);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }
}