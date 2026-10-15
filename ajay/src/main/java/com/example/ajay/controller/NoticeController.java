
package com.example.ajay.controller;

import com.example.ajay.entity.Notice;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.NoticeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
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

    // CREATE NOTICE - ADMIN ONLY
    @PostMapping
    public ResponseEntity<ResponseGlobal<Notice>> createNotice(
            @RequestBody Notice notice) {

        Notice created = noticeService.createNotice(
                getAuthenticatedEmail(), notice);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseGlobal.onSuccess(
                        "Notice created successfully", created));
    }

    // VIEW NOTICES FOR ADMIN OR THEIR STAFF
    @GetMapping
    public ResponseEntity<ResponseGlobal<List<Notice>>> getMyNotices() {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Notices fetched successfully",
                noticeService.getMyNotices(getAuthenticatedEmail())));
    }

    // VIEW ONE NOTICE
    @GetMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Notice>> getNoticeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Notice fetched successfully",
                noticeService.getMyNotice(
                        getAuthenticatedEmail(), id)));
    }

    // UPDATE NOTICE - ADMIN ONLY
    @PutMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Notice>> updateNotice(
            @PathVariable Long id,
            @RequestBody Notice notice) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Notice updated successfully",
                noticeService.updateNotice(
                        getAuthenticatedEmail(), id, notice)));
    }

    // DELETE NOTICE - ADMIN ONLY
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Void>> deleteNotice(
            @PathVariable Long id) {

        noticeService.deleteNotice(
                getAuthenticatedEmail(), id);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Notice deleted successfully", null));
    }
}
