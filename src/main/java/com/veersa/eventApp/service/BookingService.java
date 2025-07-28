package com.veersa.eventApp.service;

import com.veersa.eventApp.DTO.BookingRequest;
import com.veersa.eventApp.DTO.BookingResponse;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    List<BookingResponse> getBookingsByUser(Long userId);

    List<BookingResponse> getBookingsByEvent(Long eventId);

    String cancelBooking(Long bookingId);

    interface NotificationService {

        void bookingCreatedNotification(Long bookingId);

        void bookingCancelledNotification(Long bookingId);

        void bookingUpdatedNotification(Long bookingId);


    }
}
