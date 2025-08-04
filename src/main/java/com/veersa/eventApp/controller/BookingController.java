package com.veersa.eventApp.controller;


import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.mapper.BookingMapper;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.security.UserDetailsImpl;
import com.veersa.eventApp.service.BookingService;
import com.veersa.eventApp.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtils securityUtils;



    // get all bookings for the current user
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        Long userId= securityUtils.getCurrentUserId();
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }


    // ✅ Cancel a booking
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }


    //Get booking by Id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    // get bookings of an event
    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasAnyRole('ORGANIZER','ADMIN')")
    public ResponseEntity<List<BookingResponse>> getBookingsOfEvent(@PathVariable Long eventId){
        return ResponseEntity.ok(bookingService.getBookingsByEvent(eventId));

    }
}
