
package com.example.ajay.controller;

import com.example.ajay.entity.Event;
import com.example.ajay.response.ResponseGlobal;
import com.example.ajay.service.EventService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
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

    // CREATE EVENT - ADMIN ONLY
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseGlobal<Event>> createEvent(
            @RequestParam String eventTitle,
            @RequestParam LocalDate eventDate,
            @RequestParam String eventDescription,
            @RequestParam(required = false) MultipartFile eventPhoto)
            throws IOException {

        Event event = eventService.createEvent(
                getAuthenticatedEmail(),
                eventTitle,
                eventDate,
                eventDescription,
                eventPhoto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseGlobal.onSuccess(
                        "Event created successfully", event));
    }

    // VIEW EVENTS FOR THE ADMIN OR THEIR STAFF
    @GetMapping
    public ResponseEntity<ResponseGlobal<List<Event>>> getMyEvents() {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Events fetched successfully",
                eventService.getMyEvents(getAuthenticatedEmail())));
    }

    // VIEW ONE EVENT
    @GetMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Event>> getEventById(
            @PathVariable Long id) {

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Event fetched successfully",
                eventService.getMyEvent(getAuthenticatedEmail(), id)));
    }

    // UPDATE EVENT - ADMIN ONLY
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseGlobal<Event>> updateEvent(
            @PathVariable Long id,
            @RequestParam String eventTitle,
            @RequestParam LocalDate eventDate,
            @RequestParam String eventDescription,
            @RequestParam(required = false) MultipartFile eventPhoto)
            throws IOException {

        Event event = eventService.updateEvent(
                getAuthenticatedEmail(),
                id,
                eventTitle,
                eventDate,
                eventDescription,
                eventPhoto);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Event updated successfully", event));
    }

    // DELETE EVENT - ADMIN ONLY
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseGlobal<Void>> deleteEvent(
            @PathVariable Long id) {

        eventService.deleteEvent(getAuthenticatedEmail(), id);

        return ResponseEntity.ok(ResponseGlobal.onSuccess(
                "Event deleted successfully", null));
    }
}
