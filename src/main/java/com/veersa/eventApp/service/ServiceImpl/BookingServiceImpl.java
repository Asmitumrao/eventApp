package com.veersa.eventApp.service.ServiceImpl;

import com.itextpdf.text.pdf.PdfStructureElement;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.DTO.PaymentResponse;
import com.veersa.eventApp.exception.*;
import com.veersa.eventApp.respository.TicketRepository;
import com.veersa.eventApp.service.PaymentService;
import com.veersa.eventApp.mapper.BookingMapper;
import com.veersa.eventApp.model.*;
import com.veersa.eventApp.respository.BookingRepository;
import com.veersa.eventApp.respository.EventRepository;
import com.veersa.eventApp.respository.UserRepository;
import com.veersa.eventApp.service.BookingService;
import com.veersa.eventApp.service.NotificationService;
import com.veersa.eventApp.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private  final NotificationService notificationService;
    private final PaymentService paymentService;
    private  final SecurityUtils securityUtils;
    private final TicketRepository ticketRepository;


    @Override
    @Transactional
    public PaymentResponse initiateBooking(BookingRequest request) {
        try {
            // 1. Save booking in pending state (do not reduce seats or confirm)
            Booking pendingBooking = savePendingBooking(request);

            // ammount in rupees
            Double amountInRupees = new BigDecimal(pendingBooking.getEvent().getPricePerTicket())
                    .multiply(BigDecimal.valueOf(request.getNumberOfSeats())).doubleValue();

            // get payment link
            PaymentLink paymentLink = paymentService.createPaymentLink(
                    amountInRupees,
                    request.getUserEmail(),
                    request.getUserPhone(),
                    pendingBooking.getId()
            );

            pendingBooking.setAmount(amountInRupees);
            pendingBooking.setPaymentId(paymentLink.get("id").toString());
            bookingRepository.save(pendingBooking);

            // 2. Return payment link to the user
            PaymentResponse paymentResponse =PaymentResponse.builder()
                .paymentId(paymentLink.get("id"))
                .paymentLink(paymentLink.get("short_url"))
                .ammount(amountInRupees)
                .message("Payment link created successfully")
                .BookingId(pendingBooking.getId().toString())
                .build();


            return paymentResponse;
        } catch (RazorpayException e) {
            throw new PaymentFailedException("Error creating payment link : " + e.getMessage());
        }
    }

    @Override
    public Booking savePendingBooking(BookingRequest request) {
        // Validate user and event existence
        User user = securityUtils.getCurrentUser();
        // Check if the event exists and has available seats
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));


        if(event.getStatus().equals(EventStatus.CANCELLED)){
            throw new EventNotFoundException("Event is cancelled and not available for booking");
        }

        if (event.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new SeatsNotAvailableException("Only " + event.getAvailableSeats() + " seats are available for this event");

        }

        // Save booking as 'PENDING' (not confirmed yet)
        Booking booking = Booking.builder()
                .status(BookingStatus.PENDING)
                .user(user)
                .numberOfSeats(request.getNumberOfSeats())
                .userEmail(request.getUserEmail())
                .userPhone(request.getUserPhone())
                .event(event)
                .build();

        return bookingRepository.save(booking);
    }


    @Override
    @Transactional
    public boolean verifyPaymentAndConfirm(Long bookingId) throws RazorpayException {

        // Fetch and confirm the booking
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        //Verify payment via Razorpay API or trust callback if secure
        boolean paymentCheck =  paymentService.verifyPayment(booking);
        if(paymentCheck == false) {
            throw new PaymentFailedException("Payment verification failed for booking ID: " + bookingId);

        }


        // Check if the booking is still pending
        if (booking.getStatus() != BookingStatus.PENDING) return false;

        Event event = eventRepository.findById(booking.getEvent().getId()   ).orElseThrow();
        if (event.getAvailableSeats() < booking.getNumberOfSeats()){
            paymentService.refundPayment(bookingId);
            throw new SeatsNotAvailableException("Only " + event.getAvailableSeats() + " seats are available for this event and your payment is refunded if any");
        }

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
        try{
            notificationService.bookingCreatedNotification(savedBooking.getId());
        } catch (Exception e) {
            log.error("Error sending booking confirmation notification for booking ID: " + savedBooking.getId(), e);
        }finally {
            return true;
        }

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

        // Check if the user has permission to view bookings for this event
        User user = securityUtils.getCurrentUser();
        if (!user.getId().equals(event.getOrganizer().getId()) && !user.getRole().getName().equals("ROLE_ADMIN")) {
            throw new UnauthorizedActionException("You are not authorized to view this booking");
        }
        List<Booking> bookings = bookingRepository.findByEvent(event);
        return bookingMapper.mapToBookingResponse(bookings);

    }

    @Override
    @Transactional
    public String cancelBooking(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        User user = securityUtils.getCurrentUser();
        // Check if the user is authorized to cancel this booking

        if (!booking.getUser().getId().equals(user.getId()) && !user.getRole().getName().equals("ROLE_ADMIN")) {
            throw new UnauthorizedActionException("You are not authorized to cancel this booking");
        }
        deleteBooking(bookingId);
        return "Booking cancelled successfully";

    }

    @Override
    public BookingResponse getBookingById(Long bookingId) {
        Booking bookings =  bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        User user = securityUtils.getCurrentUser();
        if (!bookings.getUser().getId().equals(user.getId()) && !user.getRole().getName().equals("ROLE_ADMIN")) {
            throw new UnauthorizedActionException("You are not authorized to view bookings for this event");

        }
        return bookingMapper.mapToBookingResponse(bookings);
    }


    private List<Ticket> generateTickets(Booking booking, int numberOfTickets) {

        List<Ticket> tickets = new ArrayList<>();
        for(int i=0; i<numberOfTickets; i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketNumber("EVT-" + booking.getEvent().getId() + "-BKG-" + booking.getId() + "-" + UUID.randomUUID());// Or QR generator
            ticket.setCheckedIn(false);
            ticket.setUser(booking.getUser());
            ticket.setEvent(booking.getEvent());
            ticket.setBooking(booking);
            tickets.add(ticket);

        }
        return tickets;
    }

    @Transactional
    @Override
    public Void deleteBooking(Long bookingId) {
        // Check if the booking exists

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        if (booking == null) {
            throw new BookingNotFoundException("Booking not found");
        }

        // Check if the booking is already cancelled or refunded
        if (booking.getStatus().equals(BookingStatus.CANCELLED)) {
            throw new BookingNotFoundException("Booking is already cancelled or refunded");
        }

        // Set the booking status to cancelled
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Restore available seats in the event
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getNumberOfSeats());
        eventRepository.save(event);

        // delete the tickets associated with this booking
        ticketRepository.deleteByBookingId(booking.getId());

        // Notify the user about the cancellation
        try{
            notificationService.bookingCancelledNotification(booking.getId());
        } catch (Exception e) {
            log.error("Error sending booking cancellation notification for booking ID: " + booking.getId(), e);
        }
        return null;
    }

    @Override
    public List<BookingResponse> getCancelledBookingsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Booking> cancelledBookings = bookingRepository.findCancelledBookingsByUser(user);
        return bookingMapper.mapToBookingResponse(cancelledBookings);
    }
}
