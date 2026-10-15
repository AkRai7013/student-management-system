
package com.example.ajay.controller;

import com.example.ajay.DTO.StaffDTO;
import com.example.ajay.entity.Staff;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.StaffService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    private String getAuthenticatedEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {
            throw new IllegalArgumentException(
                    "Authentication required");
        }

        return authentication.getName();
    }

    // CREATE STAFF PROFILE
    @PostMapping("/create")
    public ResponseEntity<ResponseGlobal<Staff>> createStaff(
            @Valid @RequestBody StaffDTO dto) {

        Staff staff = staffService.createStaff(
                getAuthenticatedEmail(), dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseGlobal.onSuccess(
                        "Staff profile created successfully", staff));
    }

    // GET ALL STAFF OWNED BY THIS ADMIN
    @GetMapping("/all")
    public ResponseEntity<ResponseGlobal<List<Staff>>> getAllStaff() {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Staff fetched successfully",
                staffService.getAllStaff(getAuthenticatedEmail())));
    }

    // GET STAFF BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Staff>> getStaffById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Staff fetched successfully",
                staffService.getStaffById(getAuthenticatedEmail(), id)));
    }

    // UPDATE STAFF
    @PutMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Staff>> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody StaffDTO dto) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Staff updated successfully",
                staffService.updateStaff(
                        getAuthenticatedEmail(), id, dto)));
    }

    // DEACTIVATE STAFF
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponseGlobal<Staff>> deactivateStaff(
            @PathVariable Long id) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Staff deactivated successfully",
                staffService.deactivateStaff(
                        getAuthenticatedEmail(), id)));
    }
}
