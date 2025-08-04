package com.veersa.eventApp.service.ServiceImpl;

import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.exception.BookingNotFoundException;
import com.veersa.eventApp.exception.EventNotFoundException;
import com.veersa.eventApp.exception.UserNotFoundException;
import com.veersa.eventApp.service.PaymentService;
import com.veersa.eventApp.mapper.BookingMapper;
import com.veersa.eventApp.model.*;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.BookingService;
import com.veersa.eventApp.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private  final NotificationService notificationService;
    private final PaymentService paymentService;



    @Override
    public Booking savePendingBooking(BookingRequest request) {

        // Validate user and event existence
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Check if the event exists and has available seats
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (event.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new RuntimeException(request.getNumberOfSeats() + " seats available for this event");
        }

        // Save booking as 'PENDING' (not confirmed yet)
        Booking booking = Booking.builder()
                .status(BookingStatus.PENDING)
                .user(user)
                .numberOfSeats(request.getNumberOfSeats())
                .event(event)
                .build();

        return bookingRepository.save(booking);
    }


    @Override
    @Transactional
    public boolean verifyPaymentAndConfirm(Long bookingId,String paymentId) throws RazorpayException {
        //Verify payment via Razorpay API or trust callback if secure
        boolean paymentCheck =  paymentService.verifyPayment(paymentId,bookingId);
        if(paymentCheck == false) {
            System.out.println("Payment verification failed for booking ID: " + bookingId); // Debugging line
            return false;
        }
        // Fetch and confirm the booking
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        // Check if the booking is still pending
        if (booking.getStatus() != BookingStatus.PENDING) return false;

        Event event = eventRepository.findById(booking.getEvent().getId()   ).orElseThrow();
        if (event.getAvailableSeats() < booking.getNumberOfSeats()) return false;

        System.out.println("Event found: " + event);

        // Deduct seats and confirm
        event.setAvailableSeats(event.getAvailableSeats() - booking.getNumberOfSeats());
        eventRepository.save(event);

        booking.setStatus(BookingStatus.CONFIRMED);

        // create tickets for the booking
        List<Ticket> tickets = generateTickets(booking, booking.getNumberOfSeats());
        booking.setTickets(tickets);
         Booking savedBooking = bookingRepository.save(booking);

        //Notify the user about the booking creation
        notificationService.bookingCreatedNotification(savedBooking.getId());
        return true;
    }


    @Override
    public List<BookingResponse> getBookingsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookingMapper.mapToBookingResponse(bookings);
    }

    @Override
    public List<BookingResponse> getBookingsByEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        List<Booking> bookings = bookingRepository.findByEvent(event);

        return bookingMapper.mapToBookingResponse(bookings);

    }

    @Override
    public String cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EventNotFoundException("Booking not found with id: " ));

        // only the user who has booked can cancel
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found  with email: " + email));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to cancel this booking");
        }

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

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking bookings =  bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        return bookingMapper.mapToBookingResponse(bookings);
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
