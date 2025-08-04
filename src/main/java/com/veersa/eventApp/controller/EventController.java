package com.veersa.eventApp.controller;



import com.veersa.eventApp.DTO.EventCreateRequest;
import com.veersa.eventApp.DTO.EventResponse;
import com.veersa.eventApp.DTO.EventSearchRequest;
import com.veersa.eventApp.DTO.EventUpdateRequest;
import com.veersa.eventApp.service.ServiceImpl.EventServiceImpl;
import com.veersa.eventApp.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.List;

@RestController
@RequestMapping("/api/events/")
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventService;
    private final SecurityUtils securityUtils;

    // Public: Get all events
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
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
    @PostMapping
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest event) {
        return ResponseEntity.ok(eventService.createEvent(event));
    }


    // 🔐 Organizer or Admin: Update an event
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @RequestBody EventUpdateRequest event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }


    // 🔐 Organizer or Admin: Delete an event
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    // 🔐 Admin: Get all events created by a specific
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponse>> getEventsByUserId(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventsByUserId(id));
    }

    // 🔐 Organizer : Get all events created by himself
    @GetMapping("/organizer/all")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<List<EventResponse>> getEventsByUserId() {
         //user ID is obtained from the authenticated user's context
        Long userId = securityUtils.getCurrentUserId() ;
        return ResponseEntity.ok(eventService.getEventsByUserId(userId));
    }


    // 🔐 Admin: Get all events
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponse>> getAllEventsForAdmin() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }


    // Get all events by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<EventResponse>> getEventsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(eventService.getEventsByCategoryId(categoryId));
    }


}
