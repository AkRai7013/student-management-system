
package com.example.ajay.controller;

import com.example.ajay.entity.Holiday;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.HolidayService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/holidays")
public class HolidayController {

    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
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

    // CREATE HOLIDAY - ADMIN ONLY
    @PostMapping
    public ResponseEntity<ResponseGlobal<Holiday>> createHoliday(
            @RequestBody Holiday holiday) {

        Holiday created = holidayService.createHoliday(
                getAuthenticatedEmail(), holiday);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseGlobal.onSuccess(
                        "Holiday created successfully", created));
    }

    // VIEW ALL HOLIDAYS FOR THE ADMIN OR STAFF'S ADMIN
    @GetMapping
    public ResponseEntity<ResponseGlobal<List<Holiday>>> getMyHolidays() {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Holidays fetched successfully",
                holidayService.getMyHolidays(getAuthenticatedEmail())));
    }

    // VIEW ONE HOLIDAY
    @GetMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Holiday>> getHolidayById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Holiday fetched successfully",
                holidayService.getMyHolidayById(
                        getAuthenticatedEmail(), id)));
    }

    // UPDATE HOLIDAY - ADMIN ONLY
    @PutMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Holiday>> updateHoliday(
            @PathVariable Long id,
            @RequestBody Holiday holiday) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Holiday updated successfully",
                holidayService.updateHoliday(
                        getAuthenticatedEmail(), id, holiday)));
    }

    // DELETE HOLIDAY - ADMIN ONLY
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Void>> deleteHoliday(
            @PathVariable Long id) {

        holidayService.deleteHoliday(
                getAuthenticatedEmail(), id);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Holiday deleted successfully", null));
    }
}
