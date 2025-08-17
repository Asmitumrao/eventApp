package com.veersa.eventApp.controller;


import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.PaymentResponse;
import com.veersa.eventApp.service.PaymentService;
import com.veersa.eventApp.model.Booking;
import com.veersa.eventApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final BookingService bookingService;

    @GetMapping("/payment/verify")
    public ResponseEntity<?> verifyAndConfirmBooking(@RequestParam Long bookingId , @RequestParam String paymentId){

        try{
            boolean isPaymentSuccessful = bookingService.verifyPaymentAndConfirm(bookingId, paymentId);
            if (isPaymentSuccessful) {
                return ResponseEntity.ok("Payment successful and booking confirmed!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
            }
        }
        catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment: " + e.getMessage());
        }
    }

}
