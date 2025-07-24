package com.veersa.eventApp.DTO;


import lombok.Data;

@Data
public class TicketDTO {
    private Long id;
    private String ticketNumber;
    private Long bookingId; // Only the ID to avoid recursion
}
