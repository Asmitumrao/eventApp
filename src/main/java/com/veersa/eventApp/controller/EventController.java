package com.veersa.eventApp.controller;



import com.veersa.eventApp.DTO.EventCreateRequest;
import com.veersa.eventApp.DTO.EventResponse;
import com.veersa.eventApp.DTO.EventSearchRequest;
import com.veersa.eventApp.DTO.EventUpdateRequest;
import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.service.EventServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventService;


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


    // 🔐 Organizer or Admin: Create a new event
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreateRequest event) {
        System.out.println("Creating event: " + event);
        return ResponseEntity.ok(eventService.createEvent(event));
    }


    // 🔐 Organizer or Admin: Update an event
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @RequestBody EventUpdateRequest event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }


    // 🔐 Organizer or Admin: Delete an event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }


    // 🔍 Public: Filter and search events
    @GetMapping ("/search")
    public ResponseEntity<List<EventResponse>> filterAndSearchEvents(@RequestBody EventSearchRequest request) {
        return ResponseEntity.ok(eventService.filterAndSearchEvents(request));
    }


}
