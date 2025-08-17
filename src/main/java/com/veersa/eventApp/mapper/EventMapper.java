package com.veersa.eventApp.mapper;

import com.veersa.eventApp.DTO.EventCreateRequest;
import com.veersa.eventApp.DTO.EventResponse;
import com.veersa.eventApp.model.Event;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    public Event toEvent(EventCreateRequest request) {
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setLocation(request.getLocation());
        event.setAvailableSeats(request.getAvailableSeats());
        event.setPricePerTicket(request.getPricePerTicket());
        event.setOnline(request.getIsOnline());
        event.setEventImageUrl(request.getEventImageUrl());


        return event;
    }

    public EventResponse mapToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .availableSeats(event.getAvailableSeats())
                .pricePerTicket(event.getPricePerTicket())
                .isOnline(event.isOnline())
                .eventImageUrl(event.getEventImageUrl())
                .organizerName(event.getOrganizer().getUsername()) // avoid sending full User object
                .categoryName(event.getCategory() != null ? event.getCategory().getName() : null)
                .status(event.getStatus())
                .build();
    }


    public List<EventResponse>mapToEventResponse(List<Event> events) {
        return events.stream()
                .map(this::mapToEventResponse)
                .toList();
    }

}
