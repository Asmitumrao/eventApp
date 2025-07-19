package com.veersa.eventApp.controller;


import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.model.User;
import com.veersa.eventApp.security.UserDetailsImpl;
import com.veersa.eventApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 🔒 Book an event
    @PostMapping
    public ResponseEntity<BookingResponse> bookEvent(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }


    // ✅ Get all bookings (admin or user history)
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        // This method will return bookings based on the user's role
        // get the current user id  from the security context
        Long userId  =((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }


    // ✅ Cancel a booking (optional)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully");
    }
}
