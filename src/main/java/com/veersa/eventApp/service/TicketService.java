package com.veersa.eventApp.service;

import jakarta.servlet.http.HttpServletResponse;

public interface TicketService {
    void downloadTicketsForBooking(Long bookingId, HttpServletResponse response);
}
