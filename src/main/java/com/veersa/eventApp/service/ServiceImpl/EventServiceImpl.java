package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.DTO.*;
import com.veersa.eventApp.exception.CategoryNotFoundException;
import com.veersa.eventApp.exception.EventNotFoundException;
import com.veersa.eventApp.exception.UnauthorizedActionException;
import com.veersa.eventApp.exception.UserNotFoundException;
import com.veersa.eventApp.mapper.EventMapper;
import com.veersa.eventApp.messaging.RefundProducer;
import com.veersa.eventApp.model.*;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.EventCategoryService;
import com.veersa.eventApp.service.EventService;
import com.veersa.eventApp.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@Service

public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final SecurityUtils securityUtils;
    private final EventCategoryService eventCategoryService;
    private final BookingRepository bookingRepository;
    private final RefundProducer refundProducer;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<EventResponse> getAllEvents() {

        return eventMapper.mapToEventResponse(eventRepository.findAll());
    }

    @Override
    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        if(event.getStatus().equals(EventStatus.CANCELLED)){
            throw new EventNotFoundException("Event is cancelled and not available for viewing");
        }
        return eventMapper.mapToEventResponse(event);
    }

    @Override
    public EventResponse createEvent(EventCreateRequest event, MultipartFile image) {

        // get the user who created the event
        User organizer = securityUtils.getCurrentUser();

        // set the organizer of the event
        Event newEvent = eventMapper.toEvent(event);

        if (image != null && !image.isEmpty()) {

            try{
                String imageUrl = cloudinaryService.uploadFile(image);
                newEvent.setEventImageUrl(imageUrl);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage());
            }
        }

        // getting the category of the event
        if (event.getCategoryId() != null) {
            newEvent.setCategory(eventCategoryService.getCategoryById(event.getCategoryId()));
        }else{
            throw new CategoryNotFoundException("Category ID is required to create an event");
        }

        // set the organizer and save the event
        newEvent.setOrganizer(organizer);
        eventRepository.save(newEvent);
        return eventMapper.mapToEventResponse(newEvent);
    }

    @Override
    public EventResponse updateEvent(Long id, EventUpdateRequest request) {
        // Fetch the event or throw exception if not found
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));

        if(existingEvent.getStatus().equals(EventStatus.CANCELLED)){
            throw new RuntimeException("Cancelled events cannot be updated");
        }

        // Get currently authenticated user
        User authenticatedUser = securityUtils.getCurrentUser();

        // Only the organizer is allowed to update the event
        if (!existingEvent.getOrganizer().getId().equals(authenticatedUser.getId()) &&
                !authenticatedUser.getRole().getName().equals("ADMIN")) {
            throw new RuntimeException("You are not authorized to update this event");
        }


        // Update fields
        existingEvent.setName(request.getName());
        existingEvent.setDescription(request.getDescription());
        existingEvent.setStartTime(request.getStartTime());
        existingEvent.setEndTime(request.getEndTime());
        existingEvent.setAvailableSeats(request.getAvailableSeats());
        existingEvent.setLocation(request.getLocation());
        existingEvent.setPricePerTicket(request.getPricePerTicket());
        existingEvent.setOnline(request.getIsOnline());
        existingEvent.setEventImageUrl(request.getEventImageUrl());

        // Optional: update category if provided
        if (request.getCategoryId() != null) {
            EventCategory category = eventCategoryService.getCategoryById(request.getCategoryId());
            existingEvent.setCategory(category);
        }


        // Save and return response
        eventRepository.save(existingEvent);
        return eventMapper.mapToEventResponse(existingEvent);
    }



    @Override
    @Transactional
    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id).orElseThrow(
                () -> new EventNotFoundException("Event not found with id: " + id)
        );

        if(event.getStatus().equals(EventStatus.CANCELLED)) {
            throw new EventNotFoundException("Event is already cancelled");
        }

        // Check if the authenticated user is the organizer of the event
        User authenticatedUser = securityUtils.getCurrentUser();
        if (!event.getOrganizer().getId().equals(authenticatedUser.getId()) &&
                !authenticatedUser.getRole().getName().equals("ADMIN")) {
            throw new UnauthorizedActionException( "You are not authorized to delete this event");
        }

        List<Booking> bookings = bookingRepository.findByEvent(event);

        // Send refund messages for each booking
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.PENDING_REFUND);
            RefundMessage refundMessage = new RefundMessage(
                    booking.getId(),
                    booking.getUser().getId(),
                    booking.getAmount() // In Rupees
            );
            refundProducer.sendRefund(refundMessage);
        }

        // mark the event as deleted
        event.setStatus(EventStatus.CANCELLED);
        eventRepository.save(event);

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
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Event> events = eventRepository.findByOrganizer(user);
        return eventMapper.mapToEventResponse(events);
    }

    @Override
    public List<EventResponse> getEventsByCategoryId(Long categoryId) {
        List<Event> events = eventRepository.findByCategoryId(categoryId);
        if (events.isEmpty()) {
            throw new EventNotFoundException("No events found for category with id: " + categoryId);
        }
        return eventMapper.mapToEventResponse(events);
    }

    @Override
    public List<EventResponse> getUpcomingEvents() {
        return eventMapper.mapToEventResponse(eventRepository.findUpcomingEvents());
    }

    @Override
    public List<EventResponse> getPastEvents() {
        return eventMapper.mapToEventResponse(eventRepository.findPastEvents());
    }

    public List<EventResponse> getCancelledEventsByOrganizer(Long userId) {

        User currentUser = securityUtils.getCurrentUser();
        User organizer;

        // Admins must provide a userId
        if (currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            if (userId == null) {
                throw new RuntimeException("User ID is required for admin to fetch cancelled events of an organizer");
            }
            organizer = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        }
        // Organizers can only view their own events
        else if (currentUser.getRole().getName().equals("ROLE_ORGANIZER")) {
            if (userId != null && !currentUser.getId().equals(userId)) {
                throw new UnauthorizedActionException("You are not authorized to view cancelled events of other organizers");
            }
            organizer = currentUser;
        }
        else {
            throw new UnauthorizedActionException("You are not authorized to view cancelled events");
        }

        List<Event> events = eventRepository.findCancelledEventsByOrganizer(organizer);
        return eventMapper.mapToEventResponse(events);
    }


}
