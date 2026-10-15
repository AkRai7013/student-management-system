
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
    public Staff createStaff(String adminEmail, StaffDTO dto) {

        Admin owner = getAdminByEmail(adminEmail);
        String email = normalizeEmail(dto.getEmail());

        Admin account = adminRepository.findByEmail(email);

        if (account == null || account.getRole() != RoleEnum.STAFF) {
            throw new IllegalArgumentException(
                    "Create the STAFF login account first using /admin/create");
        }

        if (staffRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Staff profile already exists for this email");
        }

        Staff staff = new Staff();
        staff.setName(dto.getName().trim());
        staff.setEmail(email);
        staff.setDepartment(dto.getDepartment());
        staff.setPhone(dto.getPhone());
        staff.setStatus("ACTIVE");
        staff.setAdmin(owner);

        return staffRepository.save(staff);
    }

    // GET STAFF OWNED BY THIS ADMIN
    @Transactional(readOnly = true)
    public List<Staff> getAllStaff(String adminEmail) {
        Admin owner = getAdminByEmail(adminEmail);
        return staffRepository.findByAdmin_Id(owner.getId());
    }

    // GET ONE STAFF MEMBER OWNED BY THIS ADMIN
    @Transactional(readOnly = true)
    public Staff getStaffById(String adminEmail, Long id) {
        Admin owner = getAdminByEmail(adminEmail);

        return staffRepository.findByIdAndAdmin_Id(id, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Staff not found for this admin"));
    }

    // UPDATE STAFF PROFILE
    @Transactional
    public Staff updateStaff(
            String adminEmail, Long id, StaffDTO dto) {

        Staff staff = getStaffById(adminEmail, id);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            staff.setName(dto.getName().trim());
        }

        staff.setDepartment(dto.getDepartment());
        staff.setPhone(dto.getPhone());

        // Email changes are not supported because the login uses this email.
        return staffRepository.save(staff);
    }

    // DEACTIVATE STAFF
    @Transactional
    public Staff deactivateStaff(String adminEmail, Long id) {
        Staff staff = getStaffById(adminEmail, id);
        staff.setStatus("INACTIVE");
        return staffRepository.save(staff);
    }

    // FIND ADMIN OWNER
    private Admin getAdminByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Authenticated admin email is required");
        }

        Admin admin = adminRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT));

        if (admin == null || admin.getRole() != RoleEnum.ADMIN) {
            throw new IllegalArgumentException(
                    "Admin account not found or access denied");
        }

        return admin;
    }

    // NORMALIZE EMAIL
    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Staff email is required");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
}
