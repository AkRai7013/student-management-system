
package com.example.ajay.controller;

import com.example.ajay.entity.Attendance;
import com.example.ajay.service.AttendanceService;
import com.example.ajay.response.ResponseGlobal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in/{staffId}")
    public ResponseEntity<ResponseGlobal<Attendance>> checkIn(
            @PathVariable Long staffId) {
        try {
            Attendance attendance = attendanceService.checkIn(staffId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ResponseGlobal.onSuccess(
                            "Staff check-in successful", attendance));

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(ResponseGlobal.onError(ex.getMessage()));
        }
    }

    @PostMapping("/check-out/{staffId}")
    public ResponseEntity<ResponseGlobal<Attendance>> checkOut(
            @PathVariable Long staffId) {
        try {
            Attendance attendance = attendanceService.checkOut(staffId);

            return ResponseEntity.ok(
                    ResponseGlobal.onSuccess(
                            "Staff check-out successful", attendance));

        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(ResponseGlobal.onError(ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ResponseGlobal<List<Attendance>>> getAllAttendance() {
        return ResponseEntity.ok(
                ResponseGlobal.onSuccess(
                        "Attendance records fetched successfully",
                        attendanceService.getAllAttendance()));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<ResponseGlobal<List<Attendance>>> getAttendanceByStaffId(
            @PathVariable Long staffId) {
        try {
            return ResponseEntity.ok(
                    ResponseGlobal.onSuccess(
                            "Staff attendance fetched successfully",
                            attendanceService.getAttendanceByStaffId(staffId)));

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(ResponseGlobal.onError(ex.getMessage()));
        }
    }
}
