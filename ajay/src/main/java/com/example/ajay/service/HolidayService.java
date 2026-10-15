
package com.example.ajay.service;

import com.example.ajay.entity.Admin;
import com.example.ajay.entity.Holiday;
import com.example.ajay.repo.HolidayRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final AdminAccessService adminAccessService;

    public HolidayService(
            HolidayRepository holidayRepository,
            AdminAccessService adminAccessService) {
        this.holidayRepository = holidayRepository;
        this.adminAccessService = adminAccessService;
    }

    @Transactional
    public Holiday createHoliday(String email, Holiday holiday) {
        Admin admin = requireAdmin(email);

        holiday.setId(null);
        holiday.setAdmin(admin);

        return holidayRepository.save(holiday);
    }

    @Transactional(readOnly = true)
    public List<Holiday> getMyHolidays(String email) {
        Admin owner = adminAccessService.getOwnerAdmin(email);

        return holidayRepository
                .findByAdmin_IdOrderByHolidayDateAsc(owner.getId());
    }

    @Transactional(readOnly = true)
    public Holiday getMyHolidayById(String email, Long holidayId) {
        Admin owner = adminAccessService.getOwnerAdmin(email);

        return holidayRepository
                .findByIdAndAdmin_Id(holidayId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Holiday not found for this admin"));
    }

    @Transactional
    public Holiday updateHoliday(
            String email, Long holidayId, Holiday updatedHoliday) {

        requireAdmin(email);

        Holiday existing = getMyHolidayById(email, holidayId);

        existing.setHolidayTitle(updatedHoliday.getHolidayTitle());
        existing.setHolidayDate(updatedHoliday.getHolidayDate());
        existing.setHolidayDescription(
                updatedHoliday.getHolidayDescription());

        return holidayRepository.save(existing);
    }

    @Transactional
    public void deleteHoliday(String email, Long holidayId) {
        requireAdmin(email);

        Holiday holiday = getMyHolidayById(email, holidayId);
        holidayRepository.delete(holiday);
    }

    private Admin requireAdmin(String email) {
        Admin admin = adminAccessService.getOwnerAdmin(email);

        if (!"ADMIN".equals(admin.getRole().name())) {
            throw new IllegalArgumentException(
                    "Only admins can modify holidays");
        }

        return admin;
    }
}
