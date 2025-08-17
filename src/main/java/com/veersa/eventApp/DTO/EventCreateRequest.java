package com.veersa.eventApp.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventCreateRequest {

    @NotBlank(message = "Event name cannot be blank")
    private String name;

    @NotBlank(message = "Event description cannot be blank")
    private String description;

    @NotNull(message = "Event start time cannot be null")
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NotBlank(message = "Event location cannot be blank")
    private String location;

    @NotNull(message = "Available seats cannot be null")
    @Min(value = 1, message = "Available seats must be at least 1")
    private Integer availableSeats;

    @NotNull(message = "Price per ticket cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Double pricePerTicket;

    @NotNull(message = "Please specify if the event is online or offline")
    private Boolean isOnline;

    private String eventImageUrl;

    @NotNull(message = "Category ID cannot be null")
    private Long categoryId;
}
