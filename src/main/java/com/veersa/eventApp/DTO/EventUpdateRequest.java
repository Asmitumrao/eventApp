package com.veersa.eventApp.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventUpdateRequest {

    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotBlank(message = "Event description cannot be blank")
    private String description;

    @NotNull(message = "Event start time cannot be null")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotBlank(message = "Event location cannot be blank")
    private String location;

    @Min(value = 1, message = "Available seats must be at least 1")
    private int availableSeats;

    @Min(value = 0, message = "Price per ticket must be non-negative")
    private double pricePerTicket;


    @NotNull(message = "Please specify if the event is online or offline")
    private Boolean isOnline;

    private String eventImageUrl;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;
}
