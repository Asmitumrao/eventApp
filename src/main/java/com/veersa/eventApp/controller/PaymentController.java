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

    private final PaymentService razorpayService;
    private final BookingService bookingService;


    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiateBooking(@RequestBody BookingRequest request) throws RazorpayException {
        // 1. Save booking in pending state (do not reduce seats or confirm)
        Booking pendingBooking = bookingService.savePendingBooking(request);

        // ammount in paise
        int amountInRupees = new BigDecimal(pendingBooking.getEvent().getPricePerTicket())
                .multiply(BigDecimal.valueOf(request.getNumberOfSeats()))
                .intValue();


        // 2. Generate payment link
        PaymentResponse paymentObject = razorpayService.createPaymentLink(
                amountInRupees, // Example amount in rupees
                request.getUserEmail(),
                request.getUserPhone(),
                pendingBooking.getId()
        ) ;
        System.out.println(paymentObject.toString());
        return ResponseEntity.ok(paymentObject);
    }

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
