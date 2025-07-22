package com.veersa.eventApp.DTO;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class EventCreateRequest {

    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private int availableSeats;
    private double pricePerTicket;
    private boolean isOnline;
    private String eventImageUrl;
    private Long categoryId;
}
