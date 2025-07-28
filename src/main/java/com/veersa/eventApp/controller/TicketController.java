package com.veersa.eventApp.controller;


import com.veersa.eventApp.service.TicketService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {


    private final TicketService ticketService;

    @GetMapping("/download/{bookingId}")
    public void downloadTickets(@PathVariable Long bookingId, HttpServletResponse response) {

        ticketService.downloadTicketsForBooking(bookingId, response);
    }
}
