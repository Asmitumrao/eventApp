package com.veersa.eventApp.service.ServiceImpl;

import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.mapper.BookingMapper;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.Event;
import com.veersa.eventApp.model.Ticket;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.TicketRepository;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private  final NotificationService notificationService;




    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        // Validate user and event existence
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));


        // Check if the event exists and has available seats
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (event.getAvailableSeats() <request.getNumberOfSeats()) {
            throw new RuntimeException(request.getNumberOfSeats() + " seats available for this event");
        }

        // Update seat count
        event.setAvailableSeats(event.getAvailableSeats() - request.getNumberOfSeats());
        eventRepository.save(event);

        // Create a new booking
        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .numberOfSeats(request.getNumberOfSeats())
                .build();

        // create tickets for the booking
        List<Ticket> tickets = generateTickets(booking, request.getNumberOfSeats());
        booking.setTickets(tickets);

        Booking savedBooking = bookingRepository.save(booking);
        ticketRepository.saveAll(tickets);

        // Notify the user about the booking creation
        notificationService.bookingCreatedNotification(savedBooking.getId());
        return bookingMapper.mapToBookingResponse(savedBooking);
    }

    @Override
    public List<BookingResponse> getBookingsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookingMapper.mapToBookingResponse(bookings);
    }

    @Override
    public List<BookingResponse> getBookingsByEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Booking> bookings = bookingRepository.findByEvent(event);

        return bookingMapper.mapToBookingResponse(bookings);

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

        // Notify the user about the booking cancellation
        notificationService.bookingCancelledNotification(bookingId);
        return "Booking with ID " + bookingId + " has been cancelled successfully.";

    }


    private List<Ticket> generateTickets(Booking booking, int numberOfTickets) {

        List<Ticket> tickets = new ArrayList<>();
        for(int i=0; i<numberOfTickets; i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketNumber(UUID.randomUUID().toString()); // Or QR generator
            ticket.setCheckedIn(false);
            ticket.setUser(booking.getUser());
            ticket.setEvent(booking.getEvent());
            ticket.setBooking(booking);
            tickets.add(ticket);

        }
        return tickets;
    }
}
