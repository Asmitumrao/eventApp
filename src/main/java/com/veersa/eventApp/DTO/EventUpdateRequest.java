package com.veersa.eventApp.DTO;

import lombok.Data;

@Data
public class EventUpdateRequest {
    private String name;
    private String description;
    private String startTime; // Changed to String for easier JSON handling
    private String endTime; // Changed to String for easier JSON handling
    private String location;
    private Integer availableSeats; // Changed to Integer to allow null values
    private Double pricePerTicket; // Changed to Double to allow null values
    private Boolean isOnline; // Changed to Boolean to allow null values
    private String eventImageUrl;
    private Long categoryId;
}
