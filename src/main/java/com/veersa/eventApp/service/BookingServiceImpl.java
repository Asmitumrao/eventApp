package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{



    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;



    @Override
    public BookingResponse createBooking(BookingRequest request) {

        // Validate user and event existence
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the event exists and has available seats
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() <request.getNumberOfSeats()) {
            throw new RuntimeException(request.getNumberOfSeats() + " seats available for this event");
        }

        // Update seat count
        event.setAvailableSeats(event.getAvailableSeats() - request.getNumberOfSeats());
        eventRepository.save(event);

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .bookingTime(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .userId(user.getId())
                .eventId(event.getId())
                .numberOfSeats(request.getNumberOfSeats())
                .bookingTime(booking.getBookingTime())
                .build();
    }

    @Override
    public List<BookingResponse> getBookingsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);

        return bookings.stream()
                .map(booking -> BookingResponse.builder()
                        .bookingId(booking.getId())
                        .userId(booking.getUser().getId())
                        .eventId(booking.getEvent().getId())
                        .numberOfSeats(booking.getNumberOfSeats())
                        .bookingTime(booking.getBookingTime())
                        .build())
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Booking> bookings = bookingRepository.findByEvent(event);

        return bookings.stream()
                .map(booking -> BookingResponse.builder()
                        .bookingId(booking.getId())
                        .userId(booking.getUser().getId())
                        .eventId(booking.getEvent().getId())
                        .numberOfSeats(booking.getNumberOfSeats())
                        .bookingTime(booking.getBookingTime())
                        .build())
                .toList();
    }

    @Override
    public String cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        // Restore available seats in the event
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);

        // Delete the booking
        bookingRepository.delete(booking);

        return "Booking with ID " + bookingId + " has been cancelled successfully.";

    }
}
