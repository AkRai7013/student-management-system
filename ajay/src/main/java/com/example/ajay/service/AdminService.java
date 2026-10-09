package com.example.ajay.service;

import com.example.ajay.Configuration.JWTService;
import com.example.ajay.DTO.AdminDTO;
import com.example.ajay.DTO.AdminLoginDTO;
import com.example.ajay.ENUM.RoleEnum;
import com.example.ajay.entity.Admin;
import com.example.ajay.repo.AdminRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final AdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AdminService(
            AdminRepository adminRepo,
            PasswordEncoder passwordEncoder,
            JWTService jwtService) {

        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Create Admin/Staff account
    public String createAdmin(AdminDTO adminDTO) {

        if (adminDTO == null) {
            return "Admin details are required";
        }

        if (adminDTO.getEmail() == null
                || adminDTO.getEmail().isBlank()) {
            return "Email is required";
        }

        if (adminDTO.getPassword() == null
                || adminDTO.getPassword().isBlank()) {
            return "Password is required";
        }

        if (adminDTO.getName() == null
                || adminDTO.getName().isBlank()) {
            return "Name is required";
        }

        String email = adminDTO.getEmail().trim();

        if (adminRepo.existsByEmail(email)) {
            return "Admin email already exists";
        }

        Admin admin = new Admin();

        admin.setName(adminDTO.getName().trim());
        admin.setEmail(email);
        admin.setPassword(
                passwordEncoder.encode(adminDTO.getPassword())
        );

        // Public account creation must not grant ADMIN privileges.
        admin.setRole(RoleEnum.STAFF);

        adminRepo.save(admin);

        return "Admin account created successfully";
    }

    // Admin/Staff login
    public Map<String, Object> adminLogin(AdminLoginDTO loginDTO) {

        if (loginDTO == null
                || loginDTO.getEmail() == null
                || loginDTO.getEmail().isBlank()
                || loginDTO.getPassword() == null
                || loginDTO.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "Email and password are required"
            );
        }

        String email = loginDTO.getEmail().trim();

        Admin admin = adminRepo.findByEmail(email);

        if (admin == null
                || !passwordEncoder.matches(
                loginDTO.getPassword(),
                admin.getPassword())) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        // Generate JWT containing email, name, and role.
        String token = jwtService.generateToken(
                admin.getEmail(),
                admin.getName(),
                admin.getRole().name()
        );

        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("name", admin.getName());
        response.put("email", admin.getEmail());
        response.put("role", admin.getRole().name());

        return response;
    }
}