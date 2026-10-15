
package com.example.ajay.service;

import com.example.ajay.entity.Admin;
import com.example.ajay.entity.Notice;
import com.example.ajay.repo.NoticeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final AdminAccessService adminAccessService;

    public NoticeService(
            NoticeRepository noticeRepository,
            AdminAccessService adminAccessService) {
        this.noticeRepository = noticeRepository;
        this.adminAccessService = adminAccessService;
    }

    // CREATE NOTICE - ADMIN ONLY
    @Transactional
    public Notice createNotice(String email, Notice notice) {
        Admin admin = requireAdmin(email);

        notice.setId(null);
        notice.setAdmin(admin);

        return noticeRepository.save(notice);
    }

    // VIEW ALL NOTICES FOR ADMIN OR STAFF
    @Transactional(readOnly = true)
    public List<Notice> getMyNotices(String email) {
        Admin owner = adminAccessService.getOwnerAdmin(email);

        return noticeRepository
                .findByAdmin_IdOrderByNoticeDateDesc(owner.getId());
    }

    // VIEW ONE NOTICE
    @Transactional(readOnly = true)
    public Notice getMyNotice(String email, Long noticeId) {
        Admin owner = adminAccessService.getOwnerAdmin(email);

        return noticeRepository
                .findByIdAndAdmin_Id(noticeId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notice not found for this admin"));
    }

    // UPDATE NOTICE - ADMIN ONLY
    @Transactional
    public Notice updateNotice(
            String email, Long noticeId, Notice updatedNotice) {

        requireAdmin(email);

        Notice existing = getMyNotice(email, noticeId);

        existing.setNoticeTitle(updatedNotice.getNoticeTitle());
        existing.setNoticeDate(updatedNotice.getNoticeDate());
        existing.setNoticeDescription(
                updatedNotice.getNoticeDescription());

        return noticeRepository.save(existing);
    }

    // DELETE NOTICE - ADMIN ONLY
    @Transactional
    public void deleteNotice(String email, Long noticeId) {
        requireAdmin(email);

        Notice notice = getMyNotice(email, noticeId);
        noticeRepository.delete(notice);
    }

    private Admin requireAdmin(String email) {
        Admin admin = adminAccessService.getOwnerAdmin(email);

        if (admin.getRole() == null
                || !"ADMIN".equals(admin.getRole().name())) {
            throw new IllegalArgumentException(
                    "Only admins can modify notices");
        }

        return admin;
    }
}
