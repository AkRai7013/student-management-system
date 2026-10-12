package com.example.ajay.Configuration;

import com.example.ajay.ENUM.RoleEnum;
import com.example.ajay.entity.Admin;
import com.example.ajay.repo.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.name:}")
    private String adminName;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    public AdminBootstrap(
            AdminRepository adminRepo,
            PasswordEncoder passwordEncoder) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (adminEmail.isBlank()
                && adminName.isBlank()
                && adminPassword.isBlank()) {
            return;
        }

        if (adminName.isBlank()
                || adminEmail.isBlank()
                || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "All bootstrap ADMIN values are required"
            );
        }

        if (adminPassword.length() < 12) {
            throw new IllegalStateException(
                    "ADMIN password must be at least 12 characters"
            );
        }

        if (adminRepo.existsByRole(RoleEnum.ADMIN)) {
            System.out.println("An ADMIN account already exists.");
            return;
        }

        String email = adminEmail.trim().toLowerCase();

        if (adminRepo.existsByEmail(email)) {
            throw new IllegalStateException(
                    "This email already belongs to another account."
            );
        }

        Admin admin = new Admin();
        admin.setName(adminName.trim());
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(RoleEnum.ADMIN);

        adminRepo.save(admin);

        System.out.println("Initial ADMIN account created successfully.");
    }
}