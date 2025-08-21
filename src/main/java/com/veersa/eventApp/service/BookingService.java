package com.veersa.eventApp.service;

import com.razorpay.RazorpayException;
import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;
import com.veersa.eventApp.DTO.PaymentResponse;
import com.veersa.eventApp.model.Booking;

import java.util.List;

public interface BookingService {





    public PaymentResponse initiateBooking(BookingRequest request);

    Booking savePendingBooking(BookingRequest bookingRequest);

    public boolean verifyPaymentAndConfirm(Long bookingId,String paymentId) throws RazorpayException;

    List<BookingResponse> getBookingsByUser(Long userId);

    List<BookingResponse> getBookingsByEvent(Long eventId);

    String cancelBooking(Long bookingId);

    BookingResponse getBookingById(Long bookingId);

    Void deleteBooking(Long bookingId);

    List<BookingResponse> getCancelledBookingsByUser(Long userId);
}
