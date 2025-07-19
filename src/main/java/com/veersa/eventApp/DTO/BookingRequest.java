package com.veersa.eventApp.DTO;

import lombok.Data;

@Data
public class BookingRequest {
    private Long eventId;
    private Long userId;
    private int numberOfSeats;
}
