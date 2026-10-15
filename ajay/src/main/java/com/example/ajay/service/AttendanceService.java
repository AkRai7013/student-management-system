
package com.example.ajay.service;

import com.example.ajay.ENUM.AttendanceStatus;
import com.example.ajay.ENUM.RoleEnum;
import com.example.ajay.entity.Admin;
import com.example.ajay.entity.Attendance;
import com.example.ajay.entity.Staff;
import com.example.ajay.repo.AdminRepository;
import com.example.ajay.repo.AttendanceRepository;
import com.example.ajay.repo.StaffRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            AdminRepository adminRepository,
            StaffRepository staffRepository) {

        this.attendanceRepository = attendanceRepository;
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
    }

    private Staff validateStaff(Long staffId) {

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Staff profile not found with ID: " + staffId));

        Admin account = adminRepository.findByEmail(staff.getEmail());

        if (account == null || account.getRole() != RoleEnum.STAFF) {
            throw new IllegalArgumentException(
                    "No valid STAFF login account found for this profile");
        }

        if (!"ACTIVE".equalsIgnoreCase(staff.getStatus())) {
            throw new IllegalArgumentException(
                    "This staff profile is inactive");
        }

        return staff;
    }

    @Transactional
    public Attendance checkIn(Long staffId) {

        Staff staff = validateStaff(staffId);
        LocalDate today = LocalDate.now();

        if (attendanceRepository
                .findByStaff_IdAndAttendanceDate(staffId, today)
                .isPresent()) {

            throw new IllegalStateException(
                    "Attendance already exists for today");
        }

        Attendance attendance = new Attendance();

        attendance.setStaff(staff);
        attendance.setAttendanceDate(today);
        attendance.setCheckIn(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);

        return attendanceRepository.save(attendance);
    }

    @Transactional
    public Attendance checkOut(Long staffId) {

        validateStaff(staffId);
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByStaff_IdAndAttendanceDate(staffId, today)
                .orElseThrow(() -> new IllegalStateException(
                        "Please check in before checking out"));

        if (attendance.getCheckOut() != null) {
            throw new IllegalStateException(
                    "You have already checked out today");
        }

        attendance.setCheckOut(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendance() {

        return attendanceRepository
                .findAllByOrderByAttendanceDateDesc();
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByStaffId(Long staffId) {

        validateStaff(staffId);

        return attendanceRepository
                .findByStaff_IdOrderByAttendanceDateDesc(staffId);
    }
}
