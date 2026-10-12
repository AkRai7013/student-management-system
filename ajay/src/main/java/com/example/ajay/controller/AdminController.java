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

    // Create STAFF account
    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(
            @RequestBody AdminDTO adminDTO) {

        String result = adminService.createAdmin(adminDTO);

        if ("Staff account created successfully".equals(result)) {
            return ResponseEntity.ok(result);
        }

        if ("Admin email already exists".equals(result)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }

    // Admin/Staff login
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody AdminLoginDTO loginDTO) {

        try {
            Map<String, Object> result =
                    adminService.adminLogin(loginDTO);

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }
}