package com.example.ajay.service;

import com.example.ajay.DTO.StaffDTO;
import com.example.ajay.ENUM.RoleEnum;
import com.example.ajay.entity.Admin;
import com.example.ajay.entity.Staff;
import com.example.ajay.repo.AdminRepository;
import com.example.ajay.repo.StaffRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final AdminRepository adminRepository;

    public StaffService(
            StaffRepository staffRepository,
            AdminRepository adminRepository) {
        this.staffRepository = staffRepository;
        this.adminRepository = adminRepository;
    }

    // CREATE STAFF PROFILE
    @Transactional
    public Staff createStaff(StaffDTO dto) {

        String email = normalizeEmail(dto.getEmail());

        Admin account = adminRepository.findByEmail(email);

        if (account == null || account.getRole() != RoleEnum.STAFF) {
            throw new IllegalArgumentException(
                    "Create the STAFF login account first using /admin/create"
            );
        }

        if (staffRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Staff profile already exists for this email"
            );
        }

        Staff staff = new Staff();
        staff.setName(dto.getName().trim());
        staff.setEmail(email);
        staff.setDepartment(dto.getDepartment());
        staff.setPhone(dto.getPhone());
        staff.setStatus("ACTIVE");

        return staffRepository.save(staff);
    }

    // GET ALL STAFF
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    // GET STAFF BY ID
    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Staff not found with ID: " + id
                        )
                );
    }

    // UPDATE STAFF PROFILE
    @Transactional
    public Staff updateStaff(Long id, StaffDTO dto) {

        Staff staff = getStaffById(id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            staff.setName(dto.getName().trim());
        }

        staff.setDepartment(dto.getDepartment());
        staff.setPhone(dto.getPhone());

        // Email changes are intentionally not supported here.
        // The login account and JWT identity use the same email.

        return staffRepository.save(staff);
    }

    // DEACTIVATE STAFF
    @Transactional
    public Staff deactivateStaff(Long id) {

        Staff staff = getStaffById(id);
        staff.setStatus("INACTIVE");

        return staffRepository.save(staff);
    }

    // NORMALIZE EMAIL
    private String normalizeEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Staff email is required"
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}