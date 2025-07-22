package com.veersa.eventApp.DTO;

import com.veersa.eventApp.model.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private int availableSeats;
    private double pricePerTicket;
    private boolean isOnline;
    private String eventImageUrl;
    private String organizerName;
    private String categoryName;
    private EventStatus status;
}
