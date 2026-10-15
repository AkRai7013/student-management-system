package com.example.ajay.repo;

import com.example.ajay.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStaff_IdAndAttendanceDate(
            Long staffId,
            LocalDate attendanceDate
    );

    List<Attendance> findByStaff_IdOrderByAttendanceDateDesc(
            Long staffId
    );

    List<Attendance> findAllByOrderByAttendanceDateDesc();
}