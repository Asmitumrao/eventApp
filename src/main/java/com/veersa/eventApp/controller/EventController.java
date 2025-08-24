package com.veersa.eventApp.controller;


import com.veersa.eventApp.DTO.EventCreateRequest;
import com.veersa.eventApp.DTO.EventResponse;
import com.veersa.eventApp.DTO.EventSearchRequest;
import com.veersa.eventApp.DTO.EventUpdateRequest;
import com.veersa.eventApp.service.EventService;
import com.veersa.eventApp.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final SecurityUtils securityUtils;

    // Public: Get all events
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // Public: Get upcoming events
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponse>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    // Public: Get past events
    @GetMapping("/past")
    public ResponseEntity<List<EventResponse>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }



    // Get single event by ID
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    // 🔍 Public: Filter and search events
    @PostMapping("/search")
    public ResponseEntity<List<EventResponse>> filterAndSearchEvents(@RequestBody EventSearchRequest request) {
        return ResponseEntity.ok(eventService.filterAndSearchEvents(request));
    }

    // 🔐 Organizer or Admin: Create a new event
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ORGANIZER')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestPart("event")EventCreateRequest event,
                                                     @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(eventService.createEvent(event,image));
    }


    // 🔐 Organizer or Admin: Update an event
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,@Valid @RequestBody EventUpdateRequest event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }


    // 🔐 Organizer or Admin: Delete an event
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted successfully");
    }

    // Get all events created by a specific user
    @GetMapping("/organizer/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<EventResponse>> getEventsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventsByUserId(id));
    }

    // 🔐 Organizer : Get all events created by himself(current user)
    @GetMapping("/organizer/all")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getEventsByUserId() {
         //user ID is obtained from the authenticated user's context
        Long userId = securityUtils.getCurrentUserId() ;
        return ResponseEntity.ok(eventService.getEventsByUserId(userId));
    }

    // Get all events by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventResponse>> getEventsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(eventService.getEventsByCategoryId(categoryId));
    }

    // 🔐 Organizer or Admin: Get all cancelled events created by himself(current user)
    @GetMapping("/cancelled")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<List<EventResponse>> getCancelledEvents(@RequestParam Long userId) {

        return ResponseEntity.ok(eventService.getCancelledEventsByOrganizer(userId));
    }




}
