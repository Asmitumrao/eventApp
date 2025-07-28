package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.DTO.EventCreateRequest;
import com.veersa.eventApp.DTO.EventResponse;
import com.veersa.eventApp.DTO.EventSearchRequest;
import com.veersa.eventApp.DTO.EventUpdateRequest;
import com.veersa.eventApp.exception.EventNotFoundException;
import com.veersa.eventApp.mapper.EventMapper;
import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {


    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Override
    public List<EventResponse> getAllEvents() {
        return eventMapper.mapToEventResponse(eventRepository.findAll());
    }

    @Override
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        return eventMapper.mapToEventResponse(event);
    }

    @Override
    public EventResponse createEvent(EventCreateRequest event) {

        // get the user who created the event
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // set the organizer of the event
        Event newEvent = eventMapper.toEvent(event);
        newEvent.setOrganizer(organizer);
        eventRepository.save(newEvent);
        return eventMapper.mapToEventResponse(newEvent);
    }

    @Override
    public EventResponse updateEvent(Long id, EventUpdateRequest request) throws RuntimeException {
        Event existingEvent = eventRepository.findById(id).orElseThrow(
                () -> new EventNotFoundException("Event not found with id: " + id)
        ) ; // throws if not found

        // Check if the authenticated user is the organizer of the event
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!existingEvent.getOrganizer().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("You are not authorized to update this event");
        }

        if (request.getName() != null) {
            existingEvent.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingEvent.setDescription(request.getDescription());
        }
        if (request.getAvailableSeats() != null) {
            existingEvent.setAvailableSeats(request.getAvailableSeats());
        }
        if (request.getLocation() != null) {
            existingEvent.setLocation(request.getLocation());
        }
        if (request.getPricePerTicket() != null) {
            existingEvent.setPricePerTicket(request.getPricePerTicket());
        }
        if (request.getIsOnline() != null) {
            existingEvent.setOnline(request.getIsOnline());
        }
        if (request.getEventImageUrl() != null) {
            existingEvent.setEventImageUrl(request.getEventImageUrl());
        }
        eventRepository.save(existingEvent);

        return eventMapper.mapToEventResponse(existingEvent);
    }


    @Override
    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id).orElseThrow(
                () -> new EventNotFoundException("Event not found with id: " + id)
        );

        // Check if the authenticated user is the organizer of the event
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User authenticatedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (!event.getOrganizer().getId().equals(authenticatedUser.getId())) {
            throw new RuntimeException("You are not authorized to delete this event");

        }
        eventRepository.delete(event);

    }


    @Override
    public List<EventResponse> filterAndSearchEvents(EventSearchRequest request) {

        List<Event>events = eventRepository.searchEvents(
                request.getKeyword(),
                request.getDateAfter(),
                request.getMinAvailableSeats(),
                request.getOrganizerId()
        );

        return eventMapper.mapToEventResponse(events);
    }

    @Override
    public List<EventResponse> getEventsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<Event> events = eventRepository.findByOrganizer(user);
        return eventMapper.mapToEventResponse(events);
    }


}
