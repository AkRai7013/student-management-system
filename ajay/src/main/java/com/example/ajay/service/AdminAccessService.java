
package com.example.ajay.service;

import com.example.ajay.ENUM.RoleEnum;
import com.example.ajay.entity.Admin;
import com.example.ajay.entity.Staff;
import com.example.ajay.repo.AdminRepository;
import com.example.ajay.repo.StaffRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AdminAccessService {

    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;

    public AdminAccessService(
            AdminRepository adminRepository,
            StaffRepository staffRepository) {
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
    }

    @Transactional(readOnly = true)
    public Admin getOwnerAdmin(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated email is required");
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        Admin account = adminRepository.findByEmail(normalizedEmail);

        if (account == null || account.getRole() == null) {
            throw new IllegalArgumentException(
                    "Account not found");
        }

        // Admin accesses their own HRMS records.
        if (account.getRole() == RoleEnum.ADMIN) {
            return account;
        }

        // Staff accesses records belonging to their assigned admin.
        if (account.getRole() == RoleEnum.STAFF) {

            Staff staff = staffRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Staff profile not found"));

            if (!"ACTIVE".equalsIgnoreCase(staff.getStatus())) {
                throw new IllegalArgumentException(
                        "Staff account is inactive");
            }

            if (staff.getAdmin() == null) {
                throw new IllegalArgumentException(
                        "Staff member has no assigned admin");
            }

            return staff.getAdmin();
        }

        throw new IllegalArgumentException(
                "This account cannot access HRMS notifications");
    }
}
