package com.veersa.eventApp.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventSearchRequest {
    private String keyword;
    private LocalDateTime dateAfter;
    private Integer minAvailableSeats;
    private Long organizerId;
}
