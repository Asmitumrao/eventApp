package com.veersa.eventApp.DTO;

import lombok.Data;

@Data
public class BookingRequest {
    private Long eventId;
    private String userEmail;
    private String userPhone;
    private int numberOfSeats;
}
