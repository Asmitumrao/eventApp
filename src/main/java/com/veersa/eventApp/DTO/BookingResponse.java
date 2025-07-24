package com.veersa.eventApp.DTO;


import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class BookingResponse {

    private Long bookingId;
    private Long eventId;
    private String eventName;
    private LocalDateTime eventDateTime;

    private Long userId;
    private String username;
    private String email;
    private int numberOfSeats;
    private List<TicketDTO> tickets;

    private LocalDateTime bookingTime;
}
