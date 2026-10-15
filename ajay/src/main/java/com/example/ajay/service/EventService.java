
package com.example.ajay.service;

import com.example.ajay.entity.Admin;
import com.example.ajay.entity.Event;
import com.example.ajay.repo.EventRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AdminAccessService adminAccessService;

    public EventService(
            EventRepository eventRepository,
            AdminAccessService adminAccessService) {
        this.eventRepository = eventRepository;
        this.adminAccessService = adminAccessService;
    }

    // CREATE EVENT - ADMIN ONLY
    @Transactional
    public Event createEvent(
            String email,
            String title,
            LocalDate date,
            String description,
            MultipartFile photo) throws IOException {

        Admin admin = requireAdmin(email);

        Event event = new Event();
        event.setEventTitle(title);
        event.setEventDate(date);
        event.setEventDescription(description);
        event.setAdmin(admin);
        event.setEventPhoto(readPhoto(photo));

        return eventRepository.save(event);
    }

    // VIEW ALL EVENTS FOR ADMIN OR STAFF
    @Transactional(readOnly = true)
    public List<Event> getMyEvents(String email) {
        Admin owner = adminAccessService.getOwnerAdmin(email);

        return eventRepository
                .findByAdmin_IdOrderByEventDateAsc(owner.getId());
    }

    // VIEW ONE EVENT
    @Transactional(readOnly = true)
    public Event getMyEvent(String email, Long eventId) {
        Admin owner = adminAccessService.getOwnerAdmin(email);

        return eventRepository
                .findByIdAndAdmin_Id(eventId, owner.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Event not found for this admin"));
    }

    // UPDATE EVENT - ADMIN ONLY
    @Transactional
    public Event updateEvent(
            String email,
            Long eventId,
            String title,
            LocalDate date,
            String description,
            MultipartFile photo) throws IOException {

        requireAdmin(email);

        Event event = getMyEvent(email, eventId);

        event.setEventTitle(title);
        event.setEventDate(date);
        event.setEventDescription(description);

        if (photo != null && !photo.isEmpty()) {
            event.setEventPhoto(readPhoto(photo));
        }

        return eventRepository.save(event);
    }

    // DELETE EVENT - ADMIN ONLY
    @Transactional
    public void deleteEvent(String email, Long eventId) {
        requireAdmin(email);

        Event event = getMyEvent(email, eventId);
        eventRepository.delete(event);
    }

    private Admin requireAdmin(String email) {
        Admin admin = adminAccessService.getOwnerAdmin(email);

        if (admin.getRole() == null
                || !"ADMIN".equals(admin.getRole().name())) {
            throw new IllegalArgumentException(
                    "Only admins can modify events");
        }

        return admin;
    }

    private byte[] readPhoto(MultipartFile photo) throws IOException {
        if (photo == null || photo.isEmpty()) {
            return null;
        }

        String contentType = photo.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Only image files are allowed");
        }

        return photo.getBytes();
    }
}
