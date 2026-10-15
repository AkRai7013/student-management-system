
package com.example.ajay.controller;

import com.example.ajay.entity.Attendance;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.AttendanceService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    private String getAuthenticatedEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalArgumentException(
                    "Authentication required");
        }

        return authentication.getName();
    }

    // Staff check-in
    @PostMapping("/check-in/{staffId}")
    public ResponseEntity<ResponseGlobal<Attendance>> checkIn(
            @PathVariable Long staffId) {

        Attendance attendance = attendanceService.checkIn(
                getAuthenticatedEmail(), staffId);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Check-in successful", attendance));
    }

    // Staff check-out
    @PostMapping("/check-out/{staffId}")
    public ResponseEntity<ResponseGlobal<Attendance>> checkOut(
            @PathVariable Long staffId) {

        Attendance attendance = attendanceService.checkOut(
                getAuthenticatedEmail(), staffId);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Check-out successful", attendance));
    }

    // Admin views attendance for their own staff
    @GetMapping("/all")
    public ResponseEntity<ResponseGlobal<List<Attendance>>> getAllAttendance() {

        List<Attendance> attendance =
                attendanceService.getAllAttendance(
                        getAuthenticatedEmail());

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Attendance fetched successfully", attendance));
    }

    // View attendance for one staff member
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ResponseGlobal<List<Attendance>>>
    getAttendanceByStaffId(@PathVariable Long staffId) {

        List<Attendance> attendance =
                attendanceService.getAttendanceByStaffId(
                        getAuthenticatedEmail(), staffId);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Staff attendance fetched successfully", attendance));
    }
}
