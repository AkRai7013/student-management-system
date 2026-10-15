
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
import java.util.Locale;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AdminRepository adminRepository;
    private final StaffRepository staffRepository;
    private final AdminAccessService adminAccessService;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            AdminRepository adminRepository,
            StaffRepository staffRepository,
            AdminAccessService adminAccessService) {

        this.attendanceRepository = attendanceRepository;
        this.adminRepository = adminRepository;
        this.staffRepository = staffRepository;
        this.adminAccessService = adminAccessService;
    }

    // Validate that the staff profile has a valid STAFF login.
    private Staff validateStaff(Long staffId) {

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Staff profile not found with ID: " + staffId));

        Admin account = adminRepository.findByEmail(
                staff.getEmail().trim().toLowerCase(Locale.ROOT));

        if (account == null || account.getRole() != RoleEnum.STAFF) {
            throw new IllegalArgumentException(
                    "No valid STAFF login account found for this profile");
        }

        if (!"ACTIVE".equalsIgnoreCase(staff.getStatus())) {
            throw new IllegalArgumentException(
                    "This staff profile is inactive");
        }

        if (staff.getAdmin() == null) {
            throw new IllegalArgumentException(
                    "Staff profile has no owning admin");
        }

        return staff;
    }

    // Check that the authenticated account owns the requested staff profile.
    private Staff validateStaffOwnership(String email, Long staffId) {

        Admin owner = adminAccessService.getOwnerAdmin(email);
        Staff staff = validateStaff(staffId);

        if (!staff.getAdmin().getId().equals(owner.getId())) {
            throw new IllegalArgumentException(
                    "You cannot access another admin's staff attendance");
        }

        return staff;
    }

    // Staff can check in only for their own profile.
    @Transactional
    public Attendance checkIn(String email, Long staffId) {

        Admin account = adminRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT));

        if (account == null || account.getRole() != RoleEnum.STAFF) {
            throw new IllegalArgumentException(
                    "Only staff accounts can check in");
        }

        Staff staff = validateStaffOwnership(email, staffId);
        if (!staff.getEmail().equalsIgnoreCase(email.trim())) {
            throw new IllegalArgumentException(
                    "Staff can check in only for their own profile");
        }

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

    // Staff can check out only for their own profile.
    @Transactional
    public Attendance checkOut(String email, Long staffId) {

        Admin account = adminRepository.findByEmail(
                email.trim().toLowerCase(Locale.ROOT));

        if (account == null || account.getRole() != RoleEnum.STAFF) {
            throw new IllegalArgumentException(
                    "Only staff accounts can check out");
        }

        Staff staff = validateStaffOwnership(email, staffId);
        if (!staff.getEmail().equalsIgnoreCase(email.trim())) {
            throw new IllegalArgumentException(
                    "Staff can check out only for their own profile");
        }

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

    // Admin sees attendance only for their own staff.
    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendance(String email) {

        Admin owner = adminAccessService.getOwnerAdmin(email);

        if (owner.getRole() != RoleEnum.ADMIN) {
            throw new IllegalArgumentException(
                    "Only admins can view all staff attendance");
        }

        return attendanceRepository.findAllByOrderByAttendanceDateDesc()
                .stream()
                .filter(attendance ->
                        attendance.getStaff() != null
                                && attendance.getStaff().getAdmin() != null
                                && attendance.getStaff().getAdmin().getId()
                                .equals(owner.getId()))
                .toList();
    }

    // View attendance for one staff member belonging to this admin.
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByStaffId(
            String email, Long staffId) {

        validateStaffOwnership(email, staffId);

        return attendanceRepository
                .findByStaff_IdOrderByAttendanceDateDesc(staffId);
    }
}
