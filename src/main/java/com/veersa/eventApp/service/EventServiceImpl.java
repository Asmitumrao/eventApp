package com.veersa.eventApp.service;

import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{


    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    @Override
    public Event createEvent(Event event) {

        // get the user who created the event
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User organizer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        // set the organizer of the event
        event.setOrganizer(organizer);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Long id, Event event) {

        Event existingEvent = getEventById(id);
        existingEvent.setName(event.getName());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setDateTime(event.getDateTime());
        existingEvent.setAvailableSeats(event.getAvailableSeats());
        return eventRepository.save(existingEvent);
    }

    @Override
    public void deleteEvent(Long id) {

        Event event = getEventById(id);
        eventRepository.delete(event);

    }
}
