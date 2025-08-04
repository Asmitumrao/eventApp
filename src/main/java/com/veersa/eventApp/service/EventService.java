package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.EventCreateRequest;
import com.veersa.eventApp.DTO.EventResponse;
import com.veersa.eventApp.DTO.EventSearchRequest;
import com.veersa.eventApp.DTO.EventUpdateRequest;
import com.veersa.eventApp.model.Event;

import java.util.List;

public interface EventService {

    List<EventResponse> getAllEvents();
    EventResponse getEventById(Long id);
    EventResponse createEvent(EventCreateRequest request);
    EventResponse updateEvent(Long id, EventUpdateRequest request);
    void deleteEvent(Long id);
    List<EventResponse> filterAndSearchEvents(EventSearchRequest request);
    public List<EventResponse> getEventsByUserId(Long userId);
    List<EventResponse> getEventsByCategoryId(Long categoryId);
}
